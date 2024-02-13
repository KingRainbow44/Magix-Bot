package moe.seikimo.magixbot.commands.clip;

import moe.seikimo.magixbot.features.clip.GuildAudioRecorder;
import moe.seikimo.magixbot.utils.EmbedUtils;
import tech.xigam.cch.command.SubCommand;
import tech.xigam.cch.utils.Interaction;

import java.util.Objects;

public final class StartSubCommand extends SubCommand {
    public StartSubCommand() {
        super("start", "Starts recording audio from a voice channel.");
    }

    @Override
    public void execute(Interaction interaction) {
        var guild = interaction.getGuild();
        Objects.requireNonNull(guild);
        var settings = guild.getData();

        var audioManager = guild.getAudioManager();
        if (audioManager.getReceivingHandler() instanceof GuildAudioRecorder) {
            interaction.reply(EmbedUtils.error("Audio recording is already in progress."));
            return;
        }

        // Check if the bot is in the voice channel.
        var member = interaction.getMember();
        Objects.requireNonNull(member);
        var voiceState = member.getVoiceState();
        if (voiceState == null || !voiceState.inAudioChannel()) {
            interaction.reply(EmbedUtils.error("You must be in a voice channel to start recording audio."));
            return;
        }

        if (!audioManager.isConnected()) {
            audioManager.openAudioConnection(voiceState.getChannel());
        }

        guild.getAudioManager().setReceivingHandler(
                new GuildAudioRecorder(settings.getClipDuration()));

        interaction.reply(EmbedUtils.info("Started recording audio."));
    }
}
