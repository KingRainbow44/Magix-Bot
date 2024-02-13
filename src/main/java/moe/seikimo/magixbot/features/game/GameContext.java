package moe.seikimo.magixbot.features.game;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

public record GameContext(
        Guild guild,
        Member host,
        MessageChannel channel
) {
}
