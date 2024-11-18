package moe.seikimo.magixbot.features.game.type;

import lombok.experimental.ExtensionMethod;
import moe.seikimo.magixbot.extensions.MemberExtensions;
import moe.seikimo.magixbot.features.game.Game;
import moe.seikimo.magixbot.features.game.GameContext;
import moe.seikimo.magixbot.utils.EmbedUtils;
import moe.seikimo.magixbot.utils.GameUtils;
import moe.seikimo.magixbot.utils.ThreadUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.attribute.IThreadContainer;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ExtensionMethod(MemberExtensions.class)
public final class WordChain extends Game {
    public static final String GAME_ID = "word-chain";

    private boolean isRunning = false;
    private ThreadChannel thread;

    private Member going;
    private float seconds = 10;
    private int timer = 0;

    private final List<Member> remaining = new ArrayList<>();
    private final List<Member> hasGone = new ArrayList<>();
    private final List<String> wordChain = new ArrayList<>();

    public WordChain(GameContext context) {
        super(context);
    }

    @Override
    public void start() {
        // Create a private thread for the game.
        if (!(this.getChannel() instanceof IThreadContainer threadChannel)) {
            throw new IllegalStateException("Channel is not a guild text channel!");
        }

        super.start();

        // Assign a starting player.
        this.remaining.addAll(this.getPlayers());
        this.going = this.getRandomMember();

        // Create a thread channel.
        threadChannel.createThreadChannel("Word Chain!", true)
                .setAutoArchiveDuration(ThreadChannel.AutoArchiveDuration.TIME_1_HOUR)
                .queue(this::onChannelCreated);
    }

    /**
     * Called when the channel is created.
     *
     * @param thread The created thread.
     */
    private void onChannelCreated(ThreadChannel thread) {
        this.thread = thread;

        // Add all players to the thread.
        this.getPlayers().forEach(member -> thread.addThreadMember(member).queue());

        // Send a message to the thread.
        thread.sendMessageEmbeds(
                EmbedUtils.info("The game will start in 5s. Get ready!"),
                EmbedUtils.info("The player starting is " + this.going.getAsMention() + ".")
        ).queue();
    }

    @Override
    public void stop(boolean force) {
        if (force) {
            this.thread.delete().queue();
        } else {
            var winner = this.remaining.get(0);
            this.thread.sendMessageEmbeds(
                    EmbedUtils.error("The game has ended!"),
                    EmbedUtils.info("The winner is " + winner.getAsMention() + "!")
            ).queue();

            var wordChain = String.join(", ", this.wordChain);
            // Upload the word chain as a text file.
            this.thread.sendFiles(FileUpload.fromData(
                    wordChain.getBytes(), "final-chain.txt")).complete();

            this.thread.getManager()
                    .setArchived(true)
                    .setLocked(true)
                    .queue();

            winner.getStats().addWordChainWin();
        }

        super.stop(force);
    }

    @Override
    protected void run() {
        // Wait 5s for players to join.
        ThreadUtils.sleep(5e3);

        // Start the game.
        this.isRunning = true;
        this.thread.sendMessageEmbeds(
                EmbedUtils.info("The game is running!")
        ).complete();

        while (this.isRunning()) {
            if (this.timer++ > (this.seconds * 10)) {
                this.timer = 0;

                // Eliminate the current player.
                this.eliminatePlayer(this.going);
                continue;
            }

            ThreadUtils.sleep(100);
        }
    }

    @Override
    @SuppressWarnings("DataFlowIssue")
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!GameUtils.canContinue(this.thread.getId(), event)) return;

        // Check if Brittany is sending messages before the game starts.
        if (!this.isRunning || !this.isRunning()) {
            event.getMessage().delete().queue();
            return;
        }

        var member = event.getMember();
        var channel = event.getChannel().asThreadChannel();
        var message = event.getMessage().getContentRaw();

        // Check if the message contains any words which have already been used.
        var words = this.wordChain.stream()
                .map(str -> str.split(" "))
                .flatMap(Arrays::stream)
                .toList();
        var split = Arrays.stream(message.split(" ")).toList();
        if (words.stream().anyMatch(split::contains)) {
            // Eliminate the player.
            this.eliminatePlayer(member);
            return;
        }

        if (member.getId().equals(this.going.getId())) {
            this.timer = 0;
            this.wordChain.add(message);
            this.hasGone.add(member);

            // Check if all members have gone.
            if (this.hasGone.size() >= this.remaining.size()) {
                this.hasGone.clear();
            }

            // Decrease the time.
            if (this.seconds > 3) {
                this.seconds -= 0.1f;
            }
            // Get the next player.
            this.going = this.getRandomMember();

            // Send a message to the thread.
            this.thread.sendMessageEmbeds(
                    EmbedUtils.info(this.going.getAsMention() + " you are up!")
            ).queue();
        } else {
            event.getMessage().delete().queue();
        }
    }

    /**
     * Eliminates a player from the game.
     *
     * @param toEliminate The member to eliminate.
     */
    private void eliminatePlayer(Member toEliminate) {
        this.remaining.remove(toEliminate);

        // Check if the game is over.
        if (this.remaining.size() <= 1) {
            this.stop(false);
            return;
        }

        // Get the next player.
        this.going = this.getRandomMember();
        // Decrement the timer.
        this.seconds -= 0.5f;

        this.thread.sendMessageEmbeds(
                EmbedUtils.error(toEliminate.getAsMention() + " has been eliminated!"),
                EmbedUtils.info(this.going.getAsMention() + " you are up!")
        ).complete();
    }

    /**
     * @return A random member that hasn't played.
     */
    private Member getRandomMember() {
        return super.getRandomMember(
                member -> !this.hasGone.contains(member),
                this.remaining::contains
        );
    }
}
