package moe.seikimo.magixbot.features.game.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.ExtensionMethod;
import moe.seikimo.general.FileUtils;
import moe.seikimo.general.NumberUtils;
import moe.seikimo.magixbot.extensions.MemberExtensions;
import moe.seikimo.magixbot.features.game.Game;
import moe.seikimo.magixbot.features.game.GameContext;
import moe.seikimo.magixbot.utils.EmbedUtils;
import moe.seikimo.magixbot.utils.GameUtils;
import moe.seikimo.magixbot.utils.ImageUtils;
import moe.seikimo.magixbot.utils.ThreadUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.attribute.IThreadContainer;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel.AutoArchiveDuration;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtensionMethod(MemberExtensions.class)
public final class Wordle extends Game {
    public static final String GAME_ID = "wordle";

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private static final int ROW_OFFSET = 6;
    private static final int COLUMN_OFFSET = 5;

    private static final List<String> WORDS;
    private static final BufferedImage FONT;

    static {
        WORDS = FileUtils.readLines("wordle/words.txt");
        FONT = ImageUtils.loadImage("wordle/font.png");
    }

    private final Map<Member, GameState> states = new HashMap<>();

    private ThreadChannel thread = null;
    private BufferedImage board = null;
    private Message boardContainer = null;
    private Member going, winner;

    public Wordle(GameContext context) {
        super(context);
    }

    @Override
    public void start() {
        // Validate the number of players playing.
        if (this.getPlayers().size() != 2) {
            throw new IllegalStateException("Wordle requires exactly 2 players!");
        }

        // Create a private thread for the game.
        if (!(this.getChannel() instanceof IThreadContainer threadChannel)) {
            throw new IllegalStateException("Channel is not a guild text channel!");
        }

        super.start();

        // Assign a starting player.
        this.going = this.getRandomMember();
        // Assign each player a random word.
        this.getPlayers().forEach(member -> {
            var word = Wordle.getRandomWord();

            // Calculate the appearances of each letter.
            var appearances = new HashMap<Character, Integer>();
            for (var i = 0; i < word.length(); i++) {
                var letter = word.charAt(i);
                appearances.put(letter, appearances.getOrDefault(letter, 0) + 1);
            }

            // Create the game state.
            this.states.put(member, new GameState(
                    new WordData(word, appearances), 0
            ));
        });

        // Load the game board.
        this.board = ImageUtils.loadImage("wordle/board.png");

        // Create a thread channel.
        threadChannel.createThreadChannel("Wordle", true)
                .setAutoArchiveDuration(AutoArchiveDuration.TIME_1_HOUR)
                .queue(this::onChannelCreated);
    }

    @Override
    public void stop(boolean force) {
        if (force) {
            this.thread.delete().queue();
        } else {
            // Announce the winner.
            if (this.winner != null) {
                this.thread.sendMessageEmbeds(
                        EmbedUtils.success("The winner is %s!".formatted(this.winner.getAsMention()))
                ).queue();
            } else {
                this.thread.sendMessageEmbeds(
                        EmbedUtils.error("The game has ended in a draw!")
                ).queue();
            }

            // Reveal answer words.
            this.states.forEach((member, state) ->
                    this.thread.sendMessageEmbeds(
                            EmbedUtils.info("%s's word was: %s".formatted(
                                    member.getAsMention(), state.word.answer))
                    ).queue()
            );

            // Archive and lock the thread.
            this.thread.getManager()
                    .setArchived(true)
                    .setLocked(true)
                    .queue();

            // Update the game statistics.
            if (this.winner != null) {
                this.winner.getStats().addWordleWin();
            }
        }

        super.stop(force);
    }

    @Override
    protected void run() {
        ThreadUtils.sleep(10e3);

        // Send the board to the thread.
        var board = ImageUtils.margin(this.board, 15);
        this.thread.sendFiles(
                FileUpload.fromData(ImageUtils.toStream(board), "board.png")
        ).queue(message -> this.boardContainer = message);
    }

    /**
     * Invoked when the thread for the game is created.
     *
     * @param thread The created thread.
     */
    private void onChannelCreated(ThreadChannel thread) {
        this.thread = thread;

        // Add all players to the thread.
        this.getPlayers().forEach(member -> thread.addThreadMember(member).queue());

        // Send a message to the thread.
        thread.sendMessageEmbeds(
                EmbedUtils.info("""
                        **The game will start in 10s. Get ready!**
                        
                        Wordle is where you guess a 5 letter word in 6 guesses.
                        You and your opponent will take turns guessing words.
                        The player starting is %s.""".formatted(this.going.getAsMention()))
        ).queue();
    }

    @Override
    @SuppressWarnings("DataFlowIssue")
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!GameUtils.canContinue(this.thread.getId(), event)) return;

        // Check if the message exists.
        if (this.boardContainer == null) {
            event.getMessage().delete().queue();
            return;
        }

        var member = event.getMember();
        var channel = event.getChannel().asThreadChannel();
        var message = event.getMessage().getContentRaw();

        // Always delete the message.
        event.getMessage().delete().queue();

        // Check if the message is valid.
        message = message.toLowerCase();
        if (message.length() != 5) return;
        if (!WORDS.contains(message)) return;

        // Check if the member can continue.
        if (member.getId().equals(this.going.getId())) {
            var state = this.states.get(member);

            // Create the game board.
            this.board = Wordle.writeWord(
                    this.board,
                    state.guess,
                    this.side(member),
                    state.word, message
            );
            // Send the board to the thread.
            var board = ImageUtils.margin(this.board, 15);
            this.boardContainer.editMessageAttachments(
                    FileUpload.fromData(ImageUtils.toStream(board), "board.png")
            ).queue();
            // Increment state.guess.
            state.guess = state.guess + 1;

            // Check if answer is correct.
            if (message.equals(state.word.answer)) {
                this.winner = member;
            } else if (state.guess >= 6) {
                this.winner = this.getOtherMember();
            } else {
                this.going = this.getOtherMember();
                return;
            }

            this.stop(false);
        }
    }

    /**
     * Gets the other member playing the game.
     *
     * @return The other member.
     */
    private Member getOtherMember() {
        return this.getPlayers().stream()
                .filter(member -> !member.getId().equals(this.going.getId()))
                .findFirst()
                .orElseThrow();
    }

    /**
     * False = left, True = right.
     *
     * @param member The member to check.
     * @return The side the member is on.
     */
    private boolean side(Member member) {
        return member.getId().equals(this.getPlayers().get(1).getId());
    }

    /**
     * Gets a random word from the word list.
     *
     * @return A random word.
     */
    private static String getRandomWord() {
        return WORDS.get(NumberUtils.RANDOM.get().nextInt(WORDS.size()));
    }

    /**
     * Calculates the offset depending on the row.
     */
    private static int rowOffset(int row) {
        var offset = 0;
        for (var i = 0; i < row; i++) {
            offset += 62 + (i % 2 == 0 ? ROW_OFFSET : ROW_OFFSET - 1);
        }
        return offset;
    }

    /**
     * Writes a word to the game board.
     */
    private static BufferedImage writeWord(
            BufferedImage board,
            int row, boolean side,
            WordData data, String word
    ) {
        var appearance = new HashMap<Character, Integer>();

        var image = board;
        for (var i = 0; i < word.length(); i++) {
            var letter = word.charAt(i);

            // Figure out which letter to pull from font table.
            var alphabetOffset = ALPHABET.indexOf(letter);
            var fontOffsetX = alphabetOffset * 62;

            /// <editor-fold desc="Word Compare">
            var color = Color.DEFAULT;

            // Add to appearance.
            appearance.put(letter, appearance.getOrDefault(letter, 0) + 1);
            // Check if letter is in exact place.
            if (data.answer.charAt(i) == letter) {
                color = Color.GREEN;
            } else if (appearance.getOrDefault(letter, 0) <=
                    data.appearances.getOrDefault(letter, 0)) {
                color = Color.YELLOW;
            }

            /// </editor-fold>

            // Figure out which column to read from.
            var fontOffsetY = color.ordinal() * 62;

            // Determine our image.
            var fontImage = Wordle.FONT.getSubimage(
                    fontOffsetX, fontOffsetY, 62, 62);
            // Write the image to the board.
            image = ImageUtils.appendToImage(image, fontImage,
                    (i * 62) + (i * COLUMN_OFFSET) + (side ? 345 : 0),
                    Wordle.rowOffset(row));
        }

        return image;
    }

    @Data
    @AllArgsConstructor
    static class GameState {
        private final WordData word;
        private int guess;
    }

    record WordData(
            String answer,
            Map<Character, Integer> appearances
    ) {}

    enum Color {
        DEFAULT, GREEN, YELLOW
    }
}
