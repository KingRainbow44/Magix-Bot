package moe.seikimo.magixbot.data.models;

import com.google.gson.JsonObject;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.PostLoad;
import lombok.Data;
import moe.seikimo.data.DatabaseObject;
import moe.seikimo.general.JObject;
import moe.seikimo.magixbot.features.game.GameStatistics;

import java.util.HashMap;
import java.util.Map;

@Data
@Entity
public final class MemberModel implements DatabaseObject<GuildModel> {
    @Id private String userId;

    private Map<String, GameStatistics> gameStats;

    @Override
    public JsonObject explain() {
        return JObject.c()
                .add("userId", this.getUserId())
                .add("gameStats", this.getGameStats())
                .gson();
    }

    @PostLoad
    public void onLoad() {
        var gameStats = this.getGameStats();
        if (gameStats == null) {
            gameStats = this.gameStats = new HashMap<>();
            this.save();
        }

        gameStats.forEach((key, value) -> value.setMember(this));
    }
}
