package moe.seikimo.magixbot.data.models;

import com.google.gson.JsonObject;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
import lombok.Data;
import moe.seikimo.data.DatabaseObject;
import moe.seikimo.general.JObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Entity("users")
/* UserModel is used for global users. (does not include guilds) */
public final class UserModel implements DatabaseObject<UserModel> {
    @Id private String userId;

    @Reference(ignoreMissing = true, lazy = true)
    private List<GuildModel> guilds = new ArrayList<>();

    /**
     * @return All member data for the user.
     */
    public List<MemberModel> getMemberData() {
        return this.guilds.stream()
                .map(g -> g.getMembers().get(userId))
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public JsonObject explain() {
        var guilds = this.guilds.stream()
                .map(GuildModel::getGuildId)
                .toList();

        return JObject.c()
                .add("userId", this.getUserId())
                .add("guilds", guilds)
                .gson();
    }
}
