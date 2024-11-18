package moe.seikimo.magixbot.features.game;

import lombok.Getter;
import moe.seikimo.magixbot.features.game.type.WordChain;
import moe.seikimo.magixbot.features.game.type.WordHunt;
import moe.seikimo.magixbot.features.game.type.Wordle;
import net.dv8tion.jda.api.entities.Guild;
import tech.xigam.cch.utils.Interaction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class GameManager {
    private static final Map<String, Class<? extends Game>> games = new HashMap<>();

    @Getter private static final Map<String, String> gameNames = new HashMap<>();
    @Getter private static final Map<Guild, Game> running = new HashMap<>();

    /**
     * Detached games are not associated with a guild.
     * This means that multiple detached games can co-exist in a guild.
     */
    @Getter private static final Set<Game> detached = new HashSet<>();

    /**
     * Initializes the game manager.
     */
    public static void initialize() {
        games.put(WordChain.GAME_ID, WordChain.class);
        games.put(Wordle.GAME_ID, Wordle.class);
        games.put(WordHunt.GAME_ID, WordHunt.class);

        gameNames.put(WordChain.GAME_ID, "Word Chain");
        gameNames.put(Wordle.GAME_ID, "Wordle");
        gameNames.put(WordHunt.GAME_ID, "Word Hunt");
    }

    /**
     * Creates a new instance of the game.
     *
     * @param type The game type ID.
     * @param interaction The interaction that triggered the game.
     * @return The game instance.
     */
    public static Game createGame(String type, Interaction interaction) {
        var guild = interaction.getGuild();
        var member = interaction.getMember();
        var channel = interaction.getChannel();

        try {
            var clazz = games.get(type);

            // Determine if the game is detached or not.
            var isDetached = false;
            try {
                var field = clazz.getDeclaredField("DETACHED");
                isDetached = field.getBoolean(null);
            } catch (NoSuchFieldException ignored) {
                // If the field does not exist, the game is not detached.
            }

            // Create the game instance.
            var game = clazz
                    .getDeclaredConstructor(GameContext.class)
                    .newInstance(new GameContext(guild, member, channel, isDetached));

            // If the game is not detached, we associate it with the guild.
            if (!isDetached) {
                running.put(guild, game);
            } else {
                detached.add(game);
            }

            // Invoke the game creation event.
            game.onCreation(interaction);

            return game;
        } catch (Exception ignored) {
            return null;
        }
    }
}
