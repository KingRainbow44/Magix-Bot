package moe.seikimo.magixbot.commands.game;

import moe.seikimo.magixbot.utils.EmbedUtils;
import moe.seikimo.magixbot.utils.GameUtils;
import moe.seikimo.magixbot.utils.JDAUtils;
import tech.xigam.cch.command.SubCommand;
import tech.xigam.cch.utils.Interaction;

public final class TerminateSubCommand extends SubCommand {
    public TerminateSubCommand() {
        super("terminate", "Terminates the game.");
    }

    @Override
    public void execute(Interaction interaction) {
        if (!JDAUtils.isNotFromGuild(interaction)) return;

        // Check if the user is already in a game.
        if (!GameUtils.isInGame(interaction)) return;
        if (!GameUtils.isGameHost(interaction)) return;

        var member = interaction.getMember();
        var game = GameUtils.getGame(member);

        try {
            game.stop(true);
            interaction.reply(EmbedUtils.info("The game has been terminated."));
        } catch (Exception exception) {
            interaction.reply(EmbedUtils.error(exception.getMessage()));
        }
    }
}
