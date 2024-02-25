package moe.seikimo.magixbot.apps;

import moe.seikimo.general.FileUtils;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public final class Scraper {
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    public static void main(String[] args) throws IOException {
        // Make and clear fonts directory.
        var fonts = new File("fonts");
        if (fonts.exists()) {
            FileUtils.clearContents(fonts);
        } else {
            Files.createDirectory(fonts.toPath());
        }

        // Load whole 'alphabet.png' image as a BufferedImage.
        var image = ImageIO.read(new File("alphabet.png"));

        for (var i = 0; i < ALPHABET.length(); i++) {
            var letter = ALPHABET.charAt(i);
            var font = image.getSubimage(i * 67, 0, 62, 62);

            // Replace colors.
            for (var x = 0; x < font.getWidth(); x++) {
                for (var y = 0; y < font.getHeight(); y++) {
                    var rgb = font.getRGB(x, y);

                    if (0x787c7e == rgb || 0x787c7e - rgb <= 100) {
                        font.setRGB(x, y, 0x6aaa64);
                    }
                }
            }

            // Save the font as a file.
            var output = new File("fonts/" + letter + ".png");
            Files.createFile(output.toPath());
            ImageIO.write(font, "png", output);
        }
    }
}
