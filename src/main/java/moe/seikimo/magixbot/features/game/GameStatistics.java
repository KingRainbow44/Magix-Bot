package moe.seikimo.magixbot.features.game;

import lombok.Data;
import moe.seikimo.magixbot.data.models.MemberModel;

@Data
public final class GameStatistics {
    private transient MemberModel member;

    private int wordChainWins = 0;

    /**
     * Increments the word chain wins by 1.
     */
    public void addWordChainWin() {
        this.setWordChainWins(this.getWordChainWins() + 1);
        this.getMember().save();
    }
}
