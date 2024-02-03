package moe.seikimo.magixbot.commands;

import moe.seikimo.magixbot.Config;
import moe.seikimo.magixbot.MagixBot;
import moe.seikimo.magixbot.utils.EmbedUtils;
import tech.xigam.cch.command.Command;
import tech.xigam.cch.command.SubCommand;
import tech.xigam.cch.command.modifiers.Baseless;
import tech.xigam.cch.utils.Interaction;

public final class ConfigCommand extends Command implements Baseless {
    public ConfigCommand() {
        super("config", "Manage the bot's configuration.");

        this.registerSubCommand(new ReloadSubCommand());
    }

    @Override
    public void execute(Interaction interaction) {
        interaction.reply(EmbedUtils.info("Usage: `config <reload>`"));
    }

    private static final class ReloadSubCommand extends SubCommand {
        public ReloadSubCommand() {
            super("reload", "Reloads the bot's configuration.");
        }

        @Override
        public void execute(Interaction interaction) {
            if (!interaction.getUser().getId()
                    .equals(Config.get().getBot().ownerId())) {
                interaction.reply(EmbedUtils.error("You are not the bot owner."));
                return;
            }

            interaction.deferReply();
            Config.load(Config.fileName);
            MagixBot.getLogger().info("The configuration was reloaded from a command.");
            interaction.reply(EmbedUtils.info("The configuration has been reloaded!"));
        }
    }
}
