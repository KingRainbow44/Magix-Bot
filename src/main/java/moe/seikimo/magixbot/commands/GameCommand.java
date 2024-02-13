package moe.seikimo.magixbot.commands;

import moe.seikimo.magixbot.commands.game.CreateSubCommand;
import moe.seikimo.magixbot.commands.game.JoinSubCommand;
import moe.seikimo.magixbot.commands.game.StartSubCommand;
import moe.seikimo.magixbot.commands.game.TerminateSubCommand;
import moe.seikimo.magixbot.utils.EmbedUtils;
import moe.seikimo.magixbot.utils.JDAUtils;
import tech.xigam.cch.command.Command;
import tech.xigam.cch.command.modifiers.Baseless;
import tech.xigam.cch.command.modifiers.Limited;
import tech.xigam.cch.utils.Interaction;

public final class GameCommand extends Command implements Limited, Baseless {
    public GameCommand() {
        super("game", "Manage your chat games.");

        this.registerSubCommand(new CreateSubCommand());
        this.registerSubCommand(new StartSubCommand());
        this.registerSubCommand(new TerminateSubCommand());
        this.registerSubCommand(new JoinSubCommand());
    }

    @Override
    public void execute(Interaction interaction) {
        if (JDAUtils.isNotFromGuild(interaction)) return;

        interaction.reply(EmbedUtils.info("Usage: `game <create|start|terminate|join> [game type|game id]`"));
    }

    @Override
    public boolean isGuildOnly() {
        return true;
    }
}
