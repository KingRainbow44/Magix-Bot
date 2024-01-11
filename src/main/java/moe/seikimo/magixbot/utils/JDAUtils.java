package moe.seikimo.magixbot.utils;

import moe.seikimo.magixbot.MagixBot;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.Nullable;

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

    /**
     * Fetches a guild by its ID.
     *
     * @param id The guild ID.
     * @return The guild.
     */
    @Nullable
    static Guild getGuild(String id) {
        return MagixBot.getInstance()
                .getBotInstance()
                .getGuildById(id);
    }

    /**
     * @return The URL to the bot's profile picture.
     */
    static String getIconUrl() {
        return MagixBot.getInstance()
                .getBotInstance()
                .getSelfUser()
                .getAvatarUrl();
    }
}
