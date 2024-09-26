package moe.seikimo.magixbot.features.game;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

/**
 * The context of a game's creation.
 *
 * @param guild The guild in which the game was created in.
 * @param host The creator/host of the game instance.
 * @param channel The channel the game was created in.
 * @param detached Whether the game is detached from the guild.
 */
public record GameContext(
        Guild guild,
        Member host,
        MessageChannel channel,
        boolean detached
) {
    /**
     * Constructor for a generic guild-centered game.
     *
     * @param guild The guild in which the game was created in.
     * @param host The creator/host of the game instance.
     * @param channel The channel the game was created in.
     */
    public GameContext(Guild guild, Member host, MessageChannel channel) {
        this(guild, host, channel, false);
    }
}
