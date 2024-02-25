package moe.seikimo.magixbot.features.game.type;

import moe.seikimo.magixbot.utils.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public final class WordleTest {
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    private static final String WORD_1 = "apple"; // Yellow 'a'
    private static final String WORD_2 = "crane"; // Yellow 'a'
    private static final String WORD_3 = "magic"; // Green 'm', 'a', 'g', 'i'
    private static final String WORD_4 = "asdfs";
    private static final String WORD_5 = "thorn";
    private static final String WORD_6 = "benit";

    private static final String ANSWER = "magix";

    private static final int ROW_OFFSET = 6; // 6 pixels between rows.
    private static final int COLUMN_OFFSET = 5; // 5 pixels between columns.

    public static void main(String[] args) throws IOException {
        var board = ImageIO.read(new File("board.png"));
        var font = ImageIO.read(new File("font.png"));

        var row = 0;

        board = writeWord(board, font, row++, false, ANSWER, WORD_1);
        board = writeWord(board, font, row++, false, ANSWER, WORD_2);
        board = writeWord(board, font, row++, false, ANSWER, WORD_3);
        board = writeWord(board, font, row++, false, ANSWER, WORD_4);
        board = writeWord(board, font, row++, false, ANSWER, WORD_5);
        board = writeWord(board, font, row, false, ANSWER, WORD_6);

        row = 0;
        board = writeWord(board, font, row++, true, ANSWER, WORD_1);
        board = writeWord(board, font, row++, true, ANSWER, WORD_2);
        board = writeWord(board, font, row++, true, ANSWER, WORD_3);
        board = writeWord(board, font, row++, true, ANSWER, WORD_4);
        board = writeWord(board, font, row++, true, ANSWER, WORD_5);
        board = writeWord(board, font, row, true, ANSWER, WORD_6);

        ImageIO.write(board, "png", new File("output.png"));
    }

    private static int getRowOffset(int row) {
        var offset = 0;

        for (var i = 0; i < row; i++) {
            offset += 62 + (i % 2 == 0 ? ROW_OFFSET : ROW_OFFSET - 1);
        }

        return offset;
    }

    private static BufferedImage writeWord(
            BufferedImage board, BufferedImage font,
            int row, boolean side,
            String answer, String word
    ) {
        // Figure out which row to write to.
        var boardOffsetY = getRowOffset(row);

        var image = board;
        for (var i = 0; i < word.length(); i++) {
            var letter = word.charAt(i);

            // Figure out which letter to pull from font table.
            var alphabetOffset = ALPHABET.indexOf(letter);
            var fontOffsetX = alphabetOffset * 62;

            // Figure out which color to use.
            var color = Color.DEFAULT;
            if (answer.charAt(i) == letter) {
                color = Color.GREEN;
            } else if (ANSWER.contains(String.valueOf(letter))) {
                color = Color.YELLOW;
            }

            // Figure out which column to read from.
            var fontOffsetY = color.ordinal() * 62;

            // Determine our image.
            var fontImage = font.getSubimage(fontOffsetX, fontOffsetY, 62, 62);
            // Write the image to the board.
            image = ImageUtils.appendToImage(image, fontImage,
                    (i * 62) + (i * COLUMN_OFFSET) + (side ? 345 : 0), boardOffsetY);
        }

        return image;
    }

    enum Color {
        DEFAULT,
        GREEN,
        YELLOW
    }
}
