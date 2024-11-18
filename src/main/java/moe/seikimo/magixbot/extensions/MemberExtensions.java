package moe.seikimo.magixbot.extensions;

import lombok.experimental.ExtensionMethod;
import moe.seikimo.data.DatabaseUtils;
import moe.seikimo.magixbot.data.models.MemberModel;
import moe.seikimo.magixbot.data.models.UserModel;
import moe.seikimo.magixbot.data.models.GameStatistics;
import net.dv8tion.jda.api.entities.Member;

@ExtensionMethod(GuildExtensions.class)
public final class MemberExtensions {
    /**
     * @return The user model, from the database or a new instance.
     */
    public static UserModel getData(Member member) {
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
    public static MemberModel getScopedData(Member member) {
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
    public static GameStatistics getStats(Member member) {
        var data = MemberExtensions.getScopedData(member);
        return data.getGameStats();
    }
}
