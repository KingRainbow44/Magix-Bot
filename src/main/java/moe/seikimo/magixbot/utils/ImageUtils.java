package moe.seikimo.magixbot.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public interface ImageUtils {
    /**
     * Converts a BufferedImage to a byte array.
     *
     * @param image The image to convert.
     * @return The byte array.
     */
    static InputStream toStream(BufferedImage image) {
        try {
            var output = new ByteArrayOutputStream();
            ImageIO.write(image, "png", output);
            return new ByteArrayInputStream(output.toByteArray());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Loads an image from a file.
     *
     * @param path The path to the image.
     * @return The loaded image.
     */
    static BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Copies a BufferedImage.
     *
     * @param image The image to copy.
     * @return The copied image.
     */
    static BufferedImage copy(BufferedImage image) {
        var cm = image.getColorModel();
        var isAlphaPremultiplied = cm.isAlphaPremultiplied();
        var raster = image.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    /**
     * Converts a BufferedImage to a byte array.
     * @param image The image to write to.
     * @param imageToAppend The image to append.
     * @param x The X coordinate of the corner to append the image to.
     * @param y The Y coordinate of the corner to append the image to.
     */
    static BufferedImage appendToImage(BufferedImage image, BufferedImage imageToAppend, int x, int y) {
        var copy = copy(image);
        var graphics = copy.createGraphics();

        graphics.drawImage(imageToAppend, x, y, null);
        graphics.dispose();
        return copy;
    }

    /**
     * Applies margin to an image.
     *
     * @param image The image to apply margin to.
     * @param margin The margin to apply.
     * @return The image with margin applied.
     */
    static BufferedImage margin(BufferedImage image, int margin) {
        var width = image.getWidth() + margin * 2;
        var height = image.getHeight() + margin * 2;

        var newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        var graphics = newImage.createGraphics();

        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);
        graphics.drawImage(image, margin, margin, null);
        graphics.dispose();

        return newImage;
    }
}
