package moe.seikimo.magixbot.commands;

import moe.seikimo.magixbot.utils.EmbedUtils;
import tech.xigam.cch.command.Command;
import tech.xigam.cch.utils.Interaction;

public final class HelpCommand extends Command {
    public HelpCommand() {
        super("help", "Shows the help menu.");
    }

    @Override
    public void execute(Interaction interaction) {
        interaction.reply(EmbedUtils.info("Test!"));
    }
}
