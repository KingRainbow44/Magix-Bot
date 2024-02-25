package moe.seikimo.magixbot.features.game.type;

import moe.seikimo.magixbot.features.game.Game;
import moe.seikimo.magixbot.features.game.GameContext;
import net.dv8tion.jda.api.entities.channel.attribute.IThreadContainer;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel.AutoArchiveDuration;

public final class Wordle extends Game {
    public static final String GAME_ID = "wordle";

    private ThreadChannel thread = null;

    public Wordle(GameContext context) {
        super(context);
    }

    @Override
    public void start() {
        super.start();

        // Validate the number of players playing.
        if (this.getPlayers().size() != 2) {
            throw new IllegalStateException("Wordle requires exactly 2 players!");
        }

        // Create a private thread for the game.
        if (!(this.getChannel() instanceof IThreadContainer threadChannel)) {
            throw new IllegalStateException("Channel is not a guild text channel!");
        }

        // Create a thread channel.
        threadChannel.createThreadChannel("Wordle", true)
                .setAutoArchiveDuration(AutoArchiveDuration.TIME_1_HOUR)
                .queue(this::onChannelCreated);
    }

    @Override
    public void stop(boolean force) {
        super.stop(force);
    }

    @Override
    protected void run() {
        // TODO: Multi-threaded game logic.
    }

    /**
     * Invoked when the thread for the game is created.
     *
     * @param thread The created thread.
     */
    private void onChannelCreated(ThreadChannel thread) {
        this.thread = thread;

        // Add all players to the thread.
        this.getPlayers().forEach(member -> thread.addThreadMember(member).queue());
    }
}
