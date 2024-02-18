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

import java.util.HashMap;
import java.util.Map;

@Data
@Entity
public final class GuildModel implements DatabaseObject<GuildModel> {
    @Id private String guildId;

    private Map<String, MemberModel> members;

    private int clipDuration = 300;

    private transient Guild guild;

    @PostLoad
    public void onLoad() {
        if (this.getMembers() == null) {
            this.setMembers(new HashMap<>());
        }

        this.getMembers().values().forEach(value -> value.setGuild(this));

        this.setGuild(JDAUtils.getGuild(this.getGuildId()));
    }

    @Override
    public JsonObject explain() {
        return JObject.c()
                .add("guildId", this.getGuildId())
                .add("clipDuration", this.getClipDuration())
                .gson();
    }
}
