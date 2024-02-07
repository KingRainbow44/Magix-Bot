package moe.seikimo.magixbot.commands;

import moe.seikimo.magixbot.utils.EmbedUtils;
import moe.seikimo.magixbot.utils.JDAUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import tech.xigam.cch.command.Command;
import tech.xigam.cch.command.modifiers.Arguments;
import tech.xigam.cch.command.modifiers.Restricted;
import tech.xigam.cch.utils.Argument;
import tech.xigam.cch.utils.Interaction;

import java.util.Collection;
import java.util.List;

public final class StarboardCommand extends Command implements Arguments, Restricted {
    public StarboardCommand() {
        super("starboard", "Sets the starboard channel for the guild.");
    }

    @Override
    public void execute(Interaction interaction) {
        if (!JDAUtils.isNotFromGuild(interaction)) return;

        var channel = interaction.getArgument("channel", GuildChannel.class);
        if (channel == null) {
            interaction.reply(EmbedUtils.error("Invalid channel."));
            return;
        }

        var guild = interaction.getGuild();
        if (guild == null) {
            interaction.reply(EmbedUtils.error("Invalid guild."));
            return;
        }

        if (!(channel instanceof TextChannel textChannel)) {
            interaction.reply(EmbedUtils.error("Invalid channel type! Please specify a text channel."));
            return;
        }

        guild.getData().setStarboardChannel(textChannel);
        interaction.reply(EmbedUtils.info("Starboard channel set to " + textChannel.getAsMention() + "."));
    }

    @Override
    public Collection<Argument> getArguments() {
        return List.of(
                Argument.create("channel", "The channel to send messages to.", "channel", OptionType.CHANNEL, true, 0)
        );
    }

    @Override
    public Collection<Permission> getPermissions() {
        return List.of(Permission.MANAGE_SERVER);
    }
}
