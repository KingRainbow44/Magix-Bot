package moe.seikimo.magixbot.features.game.type;

import moe.seikimo.magixbot.features.game.Game;
import moe.seikimo.magixbot.features.game.GameContext;
import moe.seikimo.magixbot.utils.EmbedUtils;
import moe.seikimo.magixbot.utils.ThreadUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.attribute.IThreadContainer;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class WordChain extends Game {
    public static final String GAME_ID = "word-chain";

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
        super.start();

        // Create a private thread for the game.
        if (!(this.getChannel() instanceof IThreadContainer threadChannel)) {
            throw new IllegalStateException("Channel is not a guild text channel!");
        }

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
                EmbedUtils.info("The game will start in 10s. Get ready!"),
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
                    EmbedUtils.info("The winner is " + winner.getAsMention() + "!"),
                    EmbedUtils.info("The word chain was: " + String.join(", ", this.wordChain))
            ).queue();

            this.thread.getManager()
                    .setArchived(true)
                    .setLocked(true)
                    .queue();

            winner.getStats(this.getGuild()).addWordChainWin();
        }

        super.stop(force);
    }

    @Override
    protected void run() {
        // Wait 10s for players to join.
        ThreadUtils.sleep(10e3);

        // Start the game.
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
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.isFromGuild() || !event.isFromThread()) return;
        if (event.getChannelType() != ChannelType.GUILD_PRIVATE_THREAD) return;

        var channel = event.getChannel().asThreadChannel();
        var member = event.getMember();

        if (member == null) return;
        if (!channel.getId().equals(this.thread.getId())) return;

        var message = event.getMessage().getContentRaw();
        if (message.isBlank()) return;

        if (member.getId().equals(this.going.getId())) {
            this.timer = 0;
            this.wordChain.add(message);
            this.hasGone.add(member);

            // Check if all members have gone.
            if (this.hasGone.size() >= this.remaining.size()) {
                this.hasGone.clear();
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
     * Gets a random member from the game.
     *
     * @return A random member.
     */
    private Member getRandomMember() {
        var members = this.getPlayers().stream()
                .filter(member -> !this.hasGone.contains(member))
                .filter(this.remaining::contains)
                .toList();
        return members.get((int) (Math.random() * members.size()));
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
}
