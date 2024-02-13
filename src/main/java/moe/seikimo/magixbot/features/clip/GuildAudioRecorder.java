package moe.seikimo.magixbot.features.clip;

import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.CombinedAudio;
import org.jetbrains.annotations.NotNull;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class GuildAudioRecorder implements AudioReceiveHandler {
    // Formula: (duration [s] * 1000 [ms]) / 20 [ms].
    private final int maxFrames;

    private final List<byte[]> audioFrames =
            Collections.synchronizedList(new LinkedList<>());

    public GuildAudioRecorder(int maxDuration) {
        this.maxFrames = (maxDuration * 1000) / 20;
    }

    @Override
    public boolean canReceiveCombined() {
        return true;
    }

    @Override
    public void handleCombinedAudio(@NotNull CombinedAudio combinedAudio) {
        var audio = combinedAudio.getAudioData(1.0f);
        this.audioFrames.add(audio);

        var currentFrame = this.audioFrames.size();
        if (currentFrame >= this.maxFrames) {
            // Remove the first frame added.
            this.audioFrames.remove(0);
        }
    }

    /**
     * @return The audio cached in the receiver.
     */
    public byte[] flush() {
        // Copy the frames to avoid a ConcurrentModificationException.
        var frames = new LinkedList<>(this.audioFrames);

        // Combine the frames into a single byte array.
        var audio = new byte[0];
        for (var frame : frames) {
            audio = this.concat(audio, frame);
        }

        // Clear the frames.
        this.audioFrames.clear();

        return audio;
    }

    /**
     * @return The bytes of a WAV file.
     */
    public ByteArrayOutputStream flushWav() throws IOException {
        var audioData = this.flush();
        var outputStream = new ByteArrayOutputStream();

        AudioSystem.write(
                new AudioInputStream(
                        new ByteArrayInputStream(audioData),
                        AudioReceiveHandler.OUTPUT_FORMAT,
                        audioData.length),
                AudioFileFormat.Type.WAVE, outputStream
        );

        return outputStream;
    }

    /**
     * Combines two byte arrays.
     *
     * @return The combined byte array.
     */
    private byte[] concat(byte[] a, byte[] b) {
        var both = Arrays.copyOf(a, a.length + b.length);
        System.arraycopy(b, 0, both, a.length, b.length);
        return both;
    }
}
