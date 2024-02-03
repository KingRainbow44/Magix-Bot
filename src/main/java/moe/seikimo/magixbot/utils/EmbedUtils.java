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
}
