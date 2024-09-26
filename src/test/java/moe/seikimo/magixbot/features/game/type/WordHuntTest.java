package moe.seikimo.magixbot.features.game.type;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public final class WordHuntTest {
    /**
     * 2D array representing a 4x4 grid of letters.
     */
    private static final String[][] BOARD = {
            {"a", "s", "u", "i"}, // 1. Only visit positions* once.
            {"d", "n", "e", "i"}, // 2. Visit in any direction (9).
            {"w", "d", "o", "n"}, //
            {"y", "o", "v", "r"}, //
    };

    /**
     * All valid words from the Scrabble! dictionary.
     */
    private static final Set<String> VALID_WORDS = new HashSet<>();

    static {
        try {
            var wordsFile = new File("hunt/words.txt");
            var allWords = Files.readAllLines(wordsFile.toPath()).stream()
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .toList();
            VALID_WORDS.addAll(allWords);
        } catch (IOException ex) {
            log.error("Failed to read words file.", ex);
        }
    }

    @SneakyThrows
    public static void main(String[] args) {
        System.out.println("All valid words: " + VALID_WORDS.size());
        System.out.println("--------------------------------------------------");

        var startTime = System.nanoTime();
        var wordsInBoard = WordHuntTest.allWords(BOARD);
        var endTime = System.nanoTime();

        // Calculate runtime.
        var nanoRuntime = endTime - startTime;
        var runtime = Duration.ofNanos(nanoRuntime);

        System.out.println("Runtime (ns): " + nanoRuntime);
        System.out.println("Runtime (ms): " + runtime.toMillis());
        System.out.println("Runtime (s): " + runtime.toSeconds());
        System.out.println("--------------------------------------------------");
        System.out.println("Words in board: " + wordsInBoard.size());
        System.out.println("--------------------------------------------------");

        // Check for some obvious words.
        WordHuntTest.contains(wordsInBoard, "wooned");
        WordHuntTest.contains(wordsInBoard, "nones");
        WordHuntTest.contains(wordsInBoard, "rodes");
        WordHuntTest.contains(wordsInBoard, "wooed");
        WordHuntTest.contains(wordsInBoard, "dans");
        WordHuntTest.contains(wordsInBoard, "dens");

        // Print the longest words.
        var count = 0;
        for (var word : wordsInBoard) {
            if (word.length() < 6) continue;
            if (count++ >= 6) break;

            System.out.println(word);
        }
    }

    /**
     * Returns a list of all valid words on the board.
     *
     * @param board The game board having a square-grid of letters.
     * @return All valid words sorted from longest to shortest in alphabetical order.
     */
    public static List<String> allWords(String[][] board) {
        var words = new ArrayList<String>();

        // Perform a trip for every position on the board.
        for (var row = 0; row < board.length; row++) {
            for (var column = 0; column < board[row].length; column++) {
                var initial = board[row][column];
                var visited = new HashSet<Integer>();

                visited.add(WordHuntTest.encodePosition(row, column));
                words.addAll(WordHuntTest.visit(board, row, column, initial, visited));
            }
        }

        return words.stream()
                .distinct()
                .filter(word -> word.length() >= 3)
                .filter(VALID_WORDS::contains)
                .sorted()
                .sorted((a, b) -> Integer.compare(b.length(), a.length()))
                .toList();
    }

    /**
     * Visits all adjacent positions from the current position.
     * A position cannot be visited more than once.
     *
     * @param board The game board.
     * @param x The starting x-coordinate.
     * @param y The starting y-coordinate.
     * @param trip The current trip.
     *             A trip is a sequence of letters from the starting position.
     * @param visited The set of visited positions.
     * @return A list of all words from the current position.
     */
    private static List<String> visit(
            String[][] board,
            int x, int y,
            String trip,
            Set<Integer> visited
    ) {
        var words = new ArrayList<String>();

        for (var dx = -1; dx <= 1; dx++) {
            for (var dy = -1; dy <= 1; dy++) {
                // We do not need to visit the current position.
                if (dx == 0 && dy == 0) {
                    continue;
                }

                var offsetX = x + dx;
                var offsetY = y + dy;
                var offsetPos = WordHuntTest.encodePosition(offsetX, offsetY);

                // Check if the position is within the bounds of the board.
                if (!WordHuntTest.inBounds(board, offsetX, offsetY)) {
                    continue;
                }

                // Check if the position has been visited.
                if (visited.contains(offsetPos)) {
                    continue;
                }

                // Get the letter at the position.
                var letter = WordHuntTest.readPosition(board, offsetPos);

                // Append the letter to the trip.
                var nextTrip = trip + letter;
                if (!words.contains(nextTrip)) {
                    words.add(nextTrip);
                }

                // Visit adjacent positions.
                visited.add(offsetPos);
                words.addAll(visit(board, offsetX, offsetY, nextTrip, visited));
                visited.remove(offsetPos);
            }
        }

        return words;
    }

    /**
     * Encodes a 2D point into a single integer.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @return The encoded position.
     */
    private static int encodePosition(int x, int y) {
        return x << 16 | y;
    }

    /**
     * Reads a position from the board.
     *
     * @param board The game board.
     * @param position The encoded position.
     * @return The value at the position.
     * @param <T> The type of the board.
     */
    private static <T> T readPosition(T[][] board, int position) {
        int x = position >> 16;
        int y = position & 0xFFFF;
        return board[x][y];
    }

    /**
     * Checks if the position is within the bounds of the board.
     *
     * @param board The game board.
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @return True if the position is within the bounds of the board.
     * @param <T> The type of the board.
     */
    private static <T> boolean inBounds(T[][] board, int x, int y) {
        return x >= 0 && x < board.length &&
                y >= 0 && y < board[0].length;
    }

    /**
     * Throws an assertion error if the word is not found in the board.
     *
     * @param all All words in the board.
     * @param word The word to check.
     */
    private static void contains(List<String> all, String word) {
        if (!all.contains(word)) {
            throw new AssertionError("Word '" + word + "' not found in board.");
        }
    }
}
