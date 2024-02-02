package moe.seikimo.magixbot.commands;

import moe.seikimo.magixbot.Config;
import moe.seikimo.magixbot.MagixBot;
import moe.seikimo.magixbot.utils.EmbedUtils;
import moe.seikimo.magixbot.utils.JDAUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.Command.Type;
import tech.xigam.cch.command.Command;
import tech.xigam.cch.utils.Interaction;

import java.time.OffsetDateTime;

public final class HelpCommand extends Command {
    public HelpCommand() {
        super("help", "Shows the help menu.");
    }

    @Override
    public void execute(Interaction interaction) {
        var embed = new EmbedBuilder()
                .setColor(EmbedUtils.getColor())
                .setTitle("Xigam's Commands")
                .setFooter("Xigam", JDAUtils.getIconUrl())
                .setTimestamp(OffsetDateTime.now());

        var commands = MagixBot.getInstance()
                .getCommandHandler().getRegisteredCommands();
        var prefix = Config.get().getCommands().prefix();
        for (var entry : commands.entrySet()) {
            if (entry.getValue().commandType() != Type.SLASH) continue;

            embed.addField(
                    prefix + entry.getKey(),
                    entry.getValue().getDescription(),
                    false);
        }

        interaction.reply(embed.build());
    }
}
