package moe.seikimo.magixbot.features.game.type;

import lombok.extern.slf4j.Slf4j;
import moe.seikimo.magixbot.features.game.Game;
import moe.seikimo.magixbot.features.game.GameContext;
import moe.seikimo.magixbot.utils.RandomUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.attribute.IThreadContainer;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public final class WordHunt extends Game {
    public static final String GAME_ID = "word-hunt";

    private final Map<Member, List<String>> guesses = new ConcurrentHashMap<>();

    private String[][] board = null;
    private List<String> validWords = null;

    private ThreadChannel thread;

    public WordHunt(GameContext context) {
        super(context);
    }

    @Override
    public void start() {
        // Create a private thread for the game.
        if (!(this.getChannel() instanceof IThreadContainer threadChannel)) {
            throw new IllegalStateException("Channel is not a guild text channel!");
        }

        super.start();

        // Generate the board.
        this.generateBoard(4);

        // Create a list of all players.
        for (var player : this.getPlayers()) {
            this.guesses.put(player,
                    Collections.synchronizedList(new ArrayList<>()));
        }

        // Create a thread channel.
        threadChannel.createThreadChannel("Word Hunt!", true)
                .setAutoArchiveDuration(ThreadChannel.AutoArchiveDuration.TIME_1_HOUR)
                .queue(this::onChannelCreated);
    }

    @Override
    public void stop(boolean force) {
        super.stop(force);

        // Delete the thread.
        if (this.thread != null) {
            this.thread.delete().queue();
        }
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
    }

    /**
     * Generates a square board.
     *
     * @param size The size of the board.
     */
    private void generateBoard(int size) {
        var needed = (int) Math.pow(size, 2);
        var characters = RandomUtils.randomString(needed);

        this.board = new String[size][size];
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                this.board[y][x] = String.valueOf(characters.charAt(y * size + x));
            }
        }

        try {
            // Find all valid words.
            var words = this.findWords(this.board);
            this.validWords = Arrays.asList(words);
        } catch (Error error) {
            log.error("Unable to find words.", error);
        }

        // Check if the board is valid.
        if (this.validWords.isEmpty()) {
            log.warn("Failed to generate a valid board.");
            this.generateBoard(size);
        }
        if (this.validWords.size() < 15) {
            this.generateBoard(size);
        }
    }

    /**
     * Native method to find all valid Scrabble! words in a 2D board.
     * Requires the dictionary to be initialized.
     *
     * @param board The 2D board.
     * @return The list of valid words.
     */
    private native String[] findWords(String[][] board);
}
