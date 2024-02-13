package moe.seikimo.magixbot.commands.clip;

import moe.seikimo.magixbot.utils.EmbedUtils;
import tech.xigam.cch.command.SubCommand;
import tech.xigam.cch.utils.Interaction;

public final class StopSubCommand extends SubCommand {
    public StopSubCommand() {
        super("stop", "Stops recording audio from a voice channel.");
    }

    @Override
    public void execute(Interaction interaction) {
        interaction.reply(EmbedUtils.info("Stopped recording audio."));
    }
}
