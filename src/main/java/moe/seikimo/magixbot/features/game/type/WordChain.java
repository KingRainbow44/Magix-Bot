package moe.seikimo.magixbot.features.game.type;

import moe.seikimo.magixbot.features.game.Game;
import moe.seikimo.magixbot.features.game.GameContext;
import net.dv8tion.jda.api.entities.Member;

public final class WordChain extends Game {
    public static String GAME_ID = "word-chain";

    public WordChain(GameContext context) {
        super(context);
    }

    @Override
    public void start() {
        super.start();

        this.getChannel().sendMessage("Game started!").queue();
    }

    @Override
    public void stop(boolean force) {
        super.stop(force);

        this.getChannel().sendMessage("Game stopped!").queue();
    }

    @Override
    public void addPlayer(Member member) {
        super.addPlayer(member);

        this.getChannel().sendMessage("Player " + member.getAsMention() + " added!").queue();
    }
}
