package moe.seikimo.magixbot.commands.game;

import moe.seikimo.magixbot.utils.EmbedUtils;
import moe.seikimo.magixbot.utils.GameUtils;
import moe.seikimo.magixbot.utils.JDAUtils;
import tech.xigam.cch.command.SubCommand;
import tech.xigam.cch.utils.Interaction;

public final class StartSubCommand extends SubCommand {
    public StartSubCommand() {
        super("start", "Starts the game.");
    }

    @Override
    public void execute(Interaction interaction) {
        if (!JDAUtils.isNotFromGuild(interaction)) return;

        // Check if the user is already in a game.
        var member = interaction.getMember();
        var game = GameUtils.getGame(member);
        if (!GameUtils.isInGame(interaction)) return;

        if (game.isRunning()) {
            interaction.reply(EmbedUtils.error("The game is already running."));
            return;
        }

        if (!GameUtils.isGameHost(interaction)) return;

        try {
            game.start();
            interaction.reply(EmbedUtils.info("The game has been started."));
        } catch (Exception exception) {
            interaction.reply(EmbedUtils.error(exception.getMessage()));
        }
    }
}
