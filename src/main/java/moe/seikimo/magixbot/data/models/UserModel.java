package moe.seikimo.magixbot.data.models;

import com.google.gson.JsonObject;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.Data;
import moe.seikimo.data.DatabaseObject;
import moe.seikimo.general.JObject;

@Data
@Entity
/* UserModel is used for global users. (does not include guilds) */
public final class UserModel implements DatabaseObject<UserModel> {
    @Id private String userId;

    @Override
    public JsonObject explain() {
        return JObject.c()
                .add("userId", this.getUserId())
                .gson();
    }
}
