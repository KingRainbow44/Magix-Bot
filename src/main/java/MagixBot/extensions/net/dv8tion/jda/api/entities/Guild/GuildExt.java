package MagixBot.extensions.net.dv8tion.jda.api.entities.Guild;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import moe.seikimo.data.DatabaseUtils;
import moe.seikimo.magixbot.data.models.GuildModel;
import net.dv8tion.jda.api.entities.Guild;

@Extension
public final class GuildExt {
    /**
     * @return The guild model, from the database or a new instance.
     */
    public static GuildModel getData(@This Guild guild) {
        var data = DatabaseUtils.fetch(
                GuildModel.class, "guildId", guild.getId());
        if (data == null) {
            data = new GuildModel();
            data.setGuildId(guild.getId());
        }

        return data;
    }
}
