package moe.seikimo.magixbot.commands.clip;

import moe.seikimo.magixbot.MagixBot;
import moe.seikimo.magixbot.features.clip.GuildAudioRecorder;
import moe.seikimo.magixbot.utils.EmbedUtils;
import tech.xigam.cch.command.SubCommand;
import tech.xigam.cch.utils.Interaction;

import java.util.Objects;

public final class RecordSubCommand extends SubCommand {
    public RecordSubCommand() {
        super("record", "Creates a file from the audio of the voice channel.");
    }

    @Override
    public void execute(Interaction interaction) {
        var guild = interaction.getGuild();
        Objects.requireNonNull(guild);

        var audioManager = guild.getAudioManager();
        if (audioManager.getReceivingHandler() instanceof GuildAudioRecorder recorder) {
            try {
                interaction.deferReply();

                var wav = recorder.flushWav();
                interaction.reply("Clip.wav", wav.toByteArray());
                wav.close();
            } catch (Exception exception) {
                interaction.reply(EmbedUtils.error("An error occurred while flushing the audio."));
                MagixBot.getLogger().warn("Unable to save audio clip.", exception);
            }
        } else {
            interaction.reply(EmbedUtils.error("Audio recording is not in progress."));
        }
    }
}
