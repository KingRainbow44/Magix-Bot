package moe.seikimo.magixbot.extensions;

import moe.seikimo.data.DatabaseUtils;
import moe.seikimo.magixbot.data.models.GuildModel;
import net.dv8tion.jda.api.entities.Guild;

public final class GuildExtensions {
    /**
     * @return The guild model, from the database or a new instance.
     */
    public static GuildModel getData(Guild guild) {
        var data = DatabaseUtils.fetch(
                GuildModel.class, "guildId", guild.getId());
        if (data == null) {
            data = new GuildModel();
            data.setGuildId(guild.getId());
            data.save();
        }

        return data;
    }
}
