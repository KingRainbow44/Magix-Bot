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
    @Game(name = "Word Hunt")
    private int wordHuntWins = 0;

    private int wordHuntBest = 0;

    /**
     * Transient method.
     * Adds the stats of another player to this player's stats.
     */
    public void add(GameStatistics stats) {
        this.wordChainWins += stats.wordChainWins;
        this.wordleWins += stats.wordleWins;
        this.wordHuntWins += stats.wordHuntWins;
        this.wordHuntBest = Math.max(this.wordHuntBest, stats.wordHuntBest);
    }

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

    /**
     * Increments the word hunt wins by 1.
     */
    public void addWordHuntWin() {
        this.setWordHuntWins(this.getWordHuntWins() + 1);
        this.getMember().save();
    }

    /**
     * Updates the player's best score if needed.
     *
     * @param score The new score.
     */
    public void updateWordHuntScore(int score) {
        if (score > this.getWordHuntBest()) {
            this.setWordHuntBest(score);
            this.getMember().save();
        }
    }
}
