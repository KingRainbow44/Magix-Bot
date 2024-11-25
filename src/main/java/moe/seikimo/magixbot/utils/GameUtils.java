package moe.seikimo.magixbot.utils;

import moe.seikimo.magixbot.features.game.Game;
import moe.seikimo.magixbot.features.game.GameManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import tech.xigam.cch.utils.Interaction;

public interface GameUtils {
    /**
     * Checks if a member is already in game.
     *
     * @param member The member to check.
     * @return True if the member is in game, false otherwise.
     */
    static boolean isInGame(Member member) {
        return GameManager.getRunning().values().stream()
                .anyMatch(game -> game.containsPlayer(member));
    }

    /**
     * Gets the game the member is in.
     *
     * @param member The member to check.
     * @return The game the member is in.
     */
    static Game getGame(Member member) {
        return GameManager.getRunning().values().stream()
                .filter(game -> game.containsPlayer(member))
                .findFirst()
                .orElse(null);
    }

    /**
     * Checks if the interaction is in game.
     *
     * @param interaction The interaction to check.
     * @return True if the interaction is in game, false otherwise.
     */
    static boolean isInGame(Interaction interaction) {
        if (!GameUtils.isInGame(interaction.getMember())) {
            interaction.reply(EmbedUtils.error("You are not in a game."));
            return false;
        }

        return true;
    }

    /**
     * Checks if the interaction is not in game.
     *
     * @param interaction The interaction to check.
     * @return True if the interaction is not in game, false otherwise.
     */
    static boolean isNotInGame(Interaction interaction) {
        if (GameUtils.isInGame(interaction.getMember())) {
            interaction.reply(EmbedUtils.error("You are already in a game."));
            return false;
        }

        return true;
    }

    /**
     * Checks if the interaction is in game.
     *
     * @param interaction The interaction to check.
     * @return True if the interaction is in game, false otherwise.
     */
    static boolean isGameHost(Interaction interaction) {
        var member = interaction.getMember();
        var game = GameUtils.getGame(member);
        if (game == null) {
            interaction.reply(EmbedUtils.error("You are not in a game."));
            return false;
        }
        if (game.getHost() != member) {
            interaction.reply(EmbedUtils.error("You are not the host of the game."));
            return false;
        }

        return true;
    }

    /**
     * Basic checks for a game interaction.
     *
     * @param threadId The thread ID.
     * @param event The event.
     * @return True if the interaction is valid, false otherwise.
     */
    static boolean canContinue(String threadId, MessageReceivedEvent event) {
        if (!event.isFromGuild() || !event.isFromThread()) return false;
        if (event.getChannelType() != ChannelType.GUILD_PRIVATE_THREAD) return false;
        if (event.getMember() == null) return false;
        if (!event.getChannel().getId().equals(threadId)) return false;
        return !event.getMessage().getContentRaw().isBlank();
    }
}
