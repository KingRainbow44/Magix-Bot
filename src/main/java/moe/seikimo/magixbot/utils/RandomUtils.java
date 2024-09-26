package moe.seikimo.magixbot.utils;

public interface RandomUtils {
    String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    /**
     * Generates a random string of the specified length.
     *
     * @param length The length of the string.
     * @return The generated string.
     */
    static String randomString(int length) {
        var builder = new StringBuilder(length);

        for (var i = 0; i < length; i++) {
            builder.append(ALPHABET.charAt((int) (Math.random() * ALPHABET.length())));
        }

        return builder.toString();
    }
}
