package MagixBot.extensions.net.dv8tion.jda.api.entities.Member;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import moe.seikimo.data.DatabaseUtils;
import moe.seikimo.magixbot.data.models.MemberModel;
import moe.seikimo.magixbot.data.models.UserModel;
import moe.seikimo.magixbot.features.game.GameStatistics;
import net.dv8tion.jda.api.entities.Member;

@Extension
public final class MemberExt {
    /**
     * @return The user model, from the database or a new instance.
     */
    public static UserModel getData(@This Member member) {
        var data = DatabaseUtils.fetch(
                UserModel.class, "userId", member.getId());
        if (data == null) {
            data = new UserModel();
            data.setUserId(member.getId());
        }

        return data;
    }

    /**
     * @return The member model, from the database or a new instance.
     */
    public static MemberModel getScopedData(@This Member member) {
        var guild = member.getGuild();
        var data = guild.getData();

        return data.getMembers().computeIfAbsent(member.getId(), k -> {
            var model = new MemberModel();
            model.setGuild(guild.getData());
            return model;
        });
    }

    /**
     * Fetches the game statistics for a guild.
     *
     * @return The game statistics for the guild.
     */
    public static GameStatistics getStats(@This Member member) {
        var data = MemberExt.getScopedData(member);
        return data.getGameStats();
    }
}
