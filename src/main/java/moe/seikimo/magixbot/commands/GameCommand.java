package moe.seikimo.magixbot.commands;

import moe.seikimo.magixbot.commands.game.CreateSubCommand;
import moe.seikimo.magixbot.commands.game.JoinSubCommand;
import moe.seikimo.magixbot.commands.game.StartSubCommand;
import moe.seikimo.magixbot.commands.game.TerminateSubCommand;
import moe.seikimo.magixbot.utils.EmbedUtils;
import moe.seikimo.magixbot.utils.JDAUtils;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import tech.xigam.cch.command.Command;
import tech.xigam.cch.command.modifiers.Baseless;
import tech.xigam.cch.utils.Interaction;

public final class GameCommand extends Command implements Baseless {
    public GameCommand() {
        super("game", "Manage your chat games.", InteractionContextType.GUILD);

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
}
