package moe.seikimo.magixbot.utils;

import net.dv8tion.jda.api.entities.Activity;

public interface JDAUtils {
    /**
     * Creates an activity object from a string.
     *
     * @param activity The activity.
     * @param name The name.
     * @return The activity object.
     */
    static Activity createActivity(String activity, String name) {
        return switch (activity.toLowerCase()) {
            case "playing" -> Activity.playing(name);
            case "listening" -> Activity.listening(name);
            case "watching" -> Activity.watching(name);
            case "streaming" -> Activity.streaming(name, "https://youtube.com/@KingRainbow44");
            case "competing" -> Activity.competing(name);
            default -> throw new IllegalArgumentException("Invalid activity.");
        };
    }
}
