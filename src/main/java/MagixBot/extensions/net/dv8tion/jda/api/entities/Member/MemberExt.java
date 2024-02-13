package MagixBot.extensions.net.dv8tion.jda.api.entities.Member;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import moe.seikimo.data.DatabaseUtils;
import moe.seikimo.magixbot.data.models.MemberModel;
import moe.seikimo.magixbot.features.game.GameStatistics;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.util.HashMap;

@Extension
public final class MemberExt {
    /**
     * @return The member model, from the database or a new instance.
     */
    public static MemberModel getData(@This Member member) {
        var data = DatabaseUtils.fetch(
                MemberModel.class, "userId", member.getId());
        if (data == null) {
            data = new MemberModel();
            data.setUserId(member.getId());
        }

        return data;
    }

    /**
     * Fetches the game statistics for a guild.
     *
     * @param guild The guild to fetch the stats for.
     * @return The game statistics for the guild.
     */
    public static GameStatistics getStats(@This Member member, Guild guild) {
        var data = MemberExt.getData(member);
        var stats = data.getGameStats();
        if (stats == null) {
            stats = new HashMap<>();
            data.setGameStats(stats);
        }

        return stats.computeIfAbsent(guild.getId(), k -> new GameStatistics());
    }
}
