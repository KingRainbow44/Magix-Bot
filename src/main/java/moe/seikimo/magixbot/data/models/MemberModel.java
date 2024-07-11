package moe.seikimo.magixbot.data.models;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.PostLoad;
import lombok.Data;

@Data
@Entity
/* MemberModel is used for guild-scoped users. (not global) */
public final class MemberModel {
    private transient GuildModel guild;

    private GameStatistics gameStats = new GameStatistics();

    public MemberModel() {
        this.onLoad();
    }

    @PostLoad
    public void onLoad() {
        this.getGameStats().setMember(this);
    }

    /**
     * Saves this model to the database.
     */
    public void save() {
        this.getGuild().save();
    }
}
