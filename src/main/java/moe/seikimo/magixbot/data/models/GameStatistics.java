package moe.seikimo.magixbot.data.models;

import dev.morphia.annotations.Entity;
import lombok.Data;
import moe.seikimo.magixbot.data.annotations.Game;

@Data
@Entity
public final class GameStatistics {
    private transient MemberModel member;

    @Game(name = "Word Chain")
    private int wordChainWins = 0;
    @Game(name = "Wordle")
    private int wordleWins = 0;

    /**
     * Increments the word chain wins by 1.
     */
    public void addWordChainWin() {
        this.setWordChainWins(this.getWordChainWins() + 1);
        this.getMember().save();
    }

    /**
     * Increments the wordle wins by 1.
     */
    public void addWordleWin() {
        this.setWordleWins(this.getWordleWins() + 1);
        this.getMember().save();
    }
}
