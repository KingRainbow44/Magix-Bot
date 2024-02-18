package moe.seikimo.magixbot.data.models;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.PostLoad;
import lombok.Data;
import moe.seikimo.magixbot.features.game.GameStatistics;

@Data
@Entity
/* MemberModel is used for guild-scoped users. (not global) */
public final class MemberModel {
    private transient GuildModel guild;

    private GameStatistics gameStats;

    @PostLoad
    public void onLoad() {
        if (this.getGameStats() == null) {
            this.setGameStats(new GameStatistics());
        }

        this.getGameStats().setMember(this);
    }

    /**
     * Saves this model to the database.
     */
    public void save() {
        this.getGuild().save();
    }
}
