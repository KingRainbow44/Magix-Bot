package moe.seikimo.magixbot.commands.clip;

import lombok.extern.slf4j.Slf4j;
import moe.seikimo.magixbot.features.clip.GuildAudioRecorder;
import moe.seikimo.magixbot.utils.EmbedUtils;
import tech.xigam.cch.command.SubCommand;
import tech.xigam.cch.utils.Interaction;

import java.util.Objects;

@Slf4j
public final class StopSubCommand extends SubCommand {
    public StopSubCommand() {
        super("stop", "Stops recording audio from a voice channel.");
    }

    @Override
    public void execute(Interaction interaction) {
        var guild = interaction.getGuild();
        Objects.requireNonNull(guild);

        var audioManager = guild.getAudioManager();
        if (audioManager.getReceivingHandler() instanceof GuildAudioRecorder recorder) {
            try {
                recorder.close();
                audioManager.setReceivingHandler(null);
                interaction.reply(EmbedUtils.info("Stopped recording audio."));
            } catch (Exception exception) {
                interaction.reply(EmbedUtils.error("An error occurred while stopping the audio."));
                log.warn("Unable to stop audio recording.", exception);
            }
        } else {
            interaction.reply(EmbedUtils.error("Audio recording is not in progress."));
        }
    }
}
