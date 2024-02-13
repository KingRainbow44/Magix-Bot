package moe.seikimo.magixbot.commands.game;

import moe.seikimo.magixbot.features.game.GameManager;
import moe.seikimo.magixbot.utils.EmbedUtils;
import moe.seikimo.magixbot.utils.GameUtils;
import moe.seikimo.magixbot.utils.JDAUtils;
import tech.xigam.cch.command.SubCommand;
import tech.xigam.cch.utils.Interaction;

public final class JoinSubCommand extends SubCommand {
    public JoinSubCommand() {
        super("join", "Joins the game.");
    }

    @Override
    public void execute(Interaction interaction) {
        if (!JDAUtils.isNotFromGuild(interaction)) return;

        // Check if the user is already in a game.
        if (!GameUtils.isNotInGame(interaction)) return;

        var game = GameManager.getRunning().get(interaction.getGuild());
        if (game == null) {
            interaction.reply(EmbedUtils.error("There is no game running."));
            return;
        }

        try {
            game.addPlayer(interaction.getMember());
            interaction.reply(EmbedUtils.info("You have joined the game."));
        } catch (Exception exception) {
            interaction.reply(EmbedUtils.error(exception.getMessage()));
        }
    }
}
