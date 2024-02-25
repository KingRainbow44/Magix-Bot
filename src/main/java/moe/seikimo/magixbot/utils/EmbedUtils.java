package moe.seikimo.magixbot.utils;

import moe.seikimo.magixbot.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public interface EmbedUtils {
    /**
     * @return The bot's color.
     */
    static Color getColor() {
        return Color.decode(Config.get().getBot().color());
    }

    /**
     * Creates a basic info embed.
     *
     * @param content The content of the embed.
     * @return The embed.
     */
    static MessageEmbed info(String content) {
        return new EmbedBuilder()
                .setColor(EmbedUtils.getColor())
                .setDescription(content)
                .build();
    }

    /**
     * Creates a basic success embed.
     *
     * @param content The content of the embed.
     * @return The embed.
     */
    static MessageEmbed success(String content) {
        return new EmbedBuilder()
                .setColor(0x7CFF55)
                .setDescription(content)
                .build();
    }

    /**
     * Creates a basic error embed.
     *
     * @param content The content of the embed.
     * @return The embed.
     */
    static MessageEmbed error(String content) {
        return new EmbedBuilder()
                .setColor(0xfc5e53)
                .setDescription(content)
                .build();
    }
}
