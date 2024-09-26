package moe.seikimo.magixbot.features.game;

import lombok.Getter;
import moe.seikimo.magixbot.features.game.type.WordChain;
import moe.seikimo.magixbot.features.game.type.WordHunt;
import moe.seikimo.magixbot.features.game.type.Wordle;
import net.dv8tion.jda.api.entities.Guild;
import tech.xigam.cch.utils.Interaction;

import java.util.HashMap;
import java.util.Map;

public final class GameManager {
    private static final Map<String, Class<? extends Game>> games = new HashMap<>();

    @Getter private static final Map<String, String> gameNames = new HashMap<>();
    @Getter private static final Map<Guild, Game> running = new HashMap<>();

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
            var game = games.get(type)
                    .getDeclaredConstructor(GameContext.class)
                    .newInstance(new GameContext(guild, member, channel));
            running.put(guild, game);

            return game;
        } catch (Exception ignored) {
            return null;
        }
    }
}
