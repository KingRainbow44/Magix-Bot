package moe.seikimo.magixbot.data.models;

import com.google.gson.JsonObject;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.PostLoad;
import lombok.Data;
import moe.seikimo.data.DatabaseObject;
import moe.seikimo.general.JObject;
import moe.seikimo.magixbot.utils.JDAUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.HashMap;
import java.util.Map;

@Data
@Entity("guilds")
public final class GuildModel implements DatabaseObject<GuildModel> {
    @Id private String guildId;

    private Map<String, MemberModel> members;

    private int clipDuration = 300;

    private String starboardChannelId;

    private transient Guild guild;
    private transient TextChannel starboardChannel;

    /**
     * Sets the starboard channel.
     *
     * @param channel The channel.
     */
    public void setStarboardChannel(TextChannel channel) {
        this.starboardChannel = channel;
        this.starboardChannelId = channel.getId();
        this.save();
    }

    @PostLoad
    public void onLoad() {
        if (this.getMembers() == null) {
            this.setMembers(new HashMap<>());
        }

        this.getMembers().values().forEach(value -> value.setGuild(this));

        // Set transient references.
        this.setGuild(JDAUtils.getGuild(this.getGuildId()));
        this.setStarboardChannel(this.getGuild()
                .getTextChannelById(this.getStarboardChannelId()));
    }

    @Override
    public JsonObject explain() {
        return JObject.c()
                .add("guildId", this.getGuildId())
                .add("clipDuration", this.getClipDuration())
                .add("starboardChannel", this.getStarboardChannelId())
                .gson();
    }
}
