package moe.seikimo.magixbot.features.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

@Getter
@RequiredArgsConstructor
public abstract class Game extends ListenerAdapter {
    private boolean running = false;

    private final GameContext context;
    private final Thread gameThread = new Thread(this::run);
    private final List<Member> players = new LinkedList<>();

    /**
     * Adds a player to the game.
     *
     * @param member The member to add.
     */
    public void addPlayer(Member member) {
        this.players.add(member);
    }

    /**
     * Checks if player is in game.
     *
     * @param member The member to check.
     * @return True if the member is in game, false otherwise.
     */
    public final boolean containsPlayer(Member member) {
        return this.players.contains(member);
    }

    /**
     * Starts the game.
     * This method should be overridden by the game type.
     */
    public void start() {
        this.running = true;
        this.gameThread.start();
    }

    /**
     * Stops the game.
     * This method should be overridden by the game type.
     *
     * @param force If true, the game will be stopped forcefully.
     */
    public void stop(boolean force) {
        this.running = false;

        // Remove the game from the manager.
        GameManager.getRunning().remove(this.context.guild());
    }

    /**
     * The game loop.
     */
    protected void run() {

    }

    /**
     * @return The host of the game.
     */
    public final Member getHost() {
        return this.context.host();
    }

    /**
     * @return The channel the game was created in.
     */
    public final MessageChannel getChannel() {
        return this.context.channel();
    }

    /**
     * @return The guild the game was created in.
     */
    public final Guild getGuild() {
        return this.context.guild();
    }

    /**
     * Gets a random member from the game.
     *
     * @return A random member.
     */
    @SafeVarargs
    protected final Member getRandomMember(Predicate<Member>... filters) {
        var members = this.getPlayers().stream();
        for (var filter : filters) {
            members = members.filter(filter);
        }

        var list = new ArrayList<>(members.toList());
        Collections.shuffle(list);
        return list.get(0);
    }
}
