package moe.seikimo.magixbot.listeners;

import moe.seikimo.magixbot.features.game.Game;
import moe.seikimo.magixbot.features.game.GameManager;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public final class GameListener extends ListenerAdapter {
    @Override
    public void onGenericEvent(@NotNull GenericEvent event) {
        if (event instanceof GenericGuildEvent guildEvent) {
            var guild = guildEvent.getGuild();
            var game = GameManager.getRunning().get(guild);
            if (game != null) {
                this.invokeEvent(game, event);
            }
        }

        if (event instanceof GenericMessageEvent messageEvent) {
            var guild = messageEvent.getGuild();
            var game = GameManager.getRunning().get(guild);
            if (game != null) {
                this.invokeEvent(game, event);
            }
        }
    }

    /**
     * Invokes the event on the game.
     *
     * @param game The game to invoke the event on.
     * @param event The event to invoke.
     */
    private void invokeEvent(Game game, GenericEvent event) {
        if (game != null) try {
            // Find the correct event handler and call it.
            var name = event.getClass().getSimpleName()
                    .replaceAll("Event", "");
            var handler = game.getClass().getDeclaredMethod(
                    "on" + name, event.getClass());
            handler.invoke(game, event);
        } catch (Exception ignored) { }
    }
}
