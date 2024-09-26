package moe.seikimo.magixbot.features.game.type;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.*;

@Slf4j
public final class WordHuntTestV2 {
    /**
     * 2D array representing a 4x4 grid of letters.
     */
    private static final String[][] BOARD = {
            {"a", "s", "u", "i"}, // 1. Only visit positions* once.
            {"d", "n", "e", "i"}, // 2. Visit in any direction (9).
            {"w", "d", "o", "n"}, //
            {"y", "o", "v", "r"}, //
    };

    // Use a Trie for more efficient prefix checking
    private static final TrieNode WORD_TRIE = new TrieNode();

    // Pre-compute direction offsets
    private static final int[][] DIRECTIONS = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1},           {0, 1},
            {1, -1},  {1, 0},  {1, 1}
    };

    static {
        try {
            var wordsFile = new File("hunt/words.txt");
            Files.readAllLines(wordsFile.toPath()).stream()
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .forEach(WORD_TRIE::insert);
        } catch (IOException ex) {
            log.error("Failed to read words file.", ex);
        }
    }

    static class TrieNode {
        private final Map<Character, TrieNode> children = new HashMap<>();
        private boolean isEndOfWord;

        public void insert(String word) {
            TrieNode current = this;
            for (char ch : word.toCharArray()) {
                current.children.putIfAbsent(ch, new TrieNode());
                current = current.children.get(ch);
            }
            current.isEndOfWord = true;
        }

        public boolean hasPrefix(String prefix) {
            TrieNode node = this;
            for (char ch : prefix.toCharArray()) {
                if (!node.children.containsKey(ch)) {
                    return false;
                }
                node = node.children.get(ch);
            }
            return true;
        }

        public boolean isWord(String word) {
            TrieNode node = this;
            for (char ch : word.toCharArray()) {
                if (!node.children.containsKey(ch)) {
                    return false;
                }
                node = node.children.get(ch);
            }
            return node.isEndOfWord;
        }
    }

    @SneakyThrows
    public static void main(String[] args) {
        System.out.println("--------------------------------------------------");

        var startTime = System.nanoTime();
        var wordsInBoard = WordHuntTest.allWords(BOARD);
        var endTime = System.nanoTime();

        var nanoRuntime = endTime - startTime;
        var runtime = Duration.ofNanos(nanoRuntime);

        System.out.println("Runtime (ns): " + nanoRuntime);
        System.out.println("Runtime (ms): " + runtime.toMillis());
        System.out.println("Runtime (s): " + runtime.toSeconds());
        System.out.println("--------------------------------------------------");
        System.out.println("Words in board: " + wordsInBoard.size());
        System.out.println("--------------------------------------------------");

        WordHuntTestV2.contains(wordsInBoard, "wooned");
        WordHuntTestV2.contains(wordsInBoard, "nones");
        WordHuntTestV2.contains(wordsInBoard, "rodes");
        WordHuntTestV2.contains(wordsInBoard, "wooed");
        WordHuntTestV2.contains(wordsInBoard, "dans");
        WordHuntTestV2.contains(wordsInBoard, "dens");

        var count = 0;
        for (var word : wordsInBoard) {
            if (word.length() < 6) continue;
            if (count++ >= 6) break;
            System.out.println(word);
        }
    }

    public static List<String> allWords(String[][] board) {
        Set<String> words = new HashSet<>();
        boolean[][] visited = new boolean[board.length][board[0].length];
        StringBuilder currentWord = new StringBuilder();

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                visit(board, row, col, visited, currentWord, words);
            }
        }

        return words.stream()
                .filter(word -> word.length() >= 3)
                .sorted(Comparator.comparingInt(String::length).reversed()
                        .thenComparing(String::compareTo))
                .toList();
    }

    private static void visit(
            String[][] board,
            int row, int col,
            boolean[][] visited,
            StringBuilder currentWord,
            Set<String> words
    ) {
        // Base case: check if we're out of bounds or already visited
        if (!inBounds(board, row, col) || visited[row][col]) {
            return;
        }

        // Add current letter and mark as visited
        visited[row][col] = true;
        currentWord.append(board[row][col]);
        String word = currentWord.toString();

        // Only continue if this is a valid prefix
        if (WORD_TRIE.hasPrefix(word)) {
            // If it's a valid word, add it
            if (WORD_TRIE.isWord(word)) {
                words.add(word);
            }

            // Try all directions
            for (int[] direction : DIRECTIONS) {
                int newRow = row + direction[0];
                int newCol = col + direction[1];
                visit(board, newRow, newCol, visited, currentWord, words);
            }
        }

        // Backtrack
        visited[row][col] = false;
        currentWord.setLength(currentWord.length() - 1);
    }

    private static boolean inBounds(String[][] board, int row, int col) {
        return row >= 0 && row < board.length && col >= 0 && col < board[0].length;
    }

    private static void contains(List<String> all, String word) {
        if (!all.contains(word)) {
            throw new AssertionError("Word '" + word + "' not found in board.");
        }
    }
}
