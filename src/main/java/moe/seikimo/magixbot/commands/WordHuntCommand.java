package moe.seikimo.magixbot.commands;

import moe.seikimo.magixbot.utils.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import tech.xigam.cch.command.Command;
import tech.xigam.cch.command.modifiers.Arguments;
import tech.xigam.cch.command.modifiers.Completable;
import tech.xigam.cch.utils.Argument;
import tech.xigam.cch.utils.Completion;
import tech.xigam.cch.utils.Interaction;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class WordHuntCommand extends Command implements Arguments, Completable {
    public WordHuntCommand() {
        super("wordhunt", "Invite a player to play Word Hunt!", InteractionContextType.PRIVATE_CHANNEL);
    }

    @Override
    public void execute(Interaction interaction) {
        var game = interaction.getArgument("game", String.class);

        var rules = "Time limit: 1m30s" +
                "\n" +
                "Game expires in " +
                EmbedUtils.relativeTime(30, TimeUnit.MINUTES);

        var embed = new EmbedBuilder()
                .setTitle("Let's Play Word Hunt!")
                .setDescription(interaction.getUser().getAsMention() + " has invited you to play Word Hunt! (game id: " + game + ")")
                .addField("Game Rules", rules, false)
                .addField("Highest Score", "prob like 35k", true)
                .addField("Total Players", "like 1 lol", true)
                .setColor(EmbedUtils.getColor())
                .build();

        interaction
                .addButtons(
                        Button.primary("join", "Game On!"))
                .reply(embed);
    }

    @Override
    public void complete(Completion completion) {
        completion
                .addChoice("an-option", "The first option")
                .addChoice("another-option", "The second option")
                .reply();
    }

    @Override
    public Collection<Argument> getArguments() {
        return List.of(
                Argument.create("game", "The game to send the player.", "game", OptionType.STRING, true, 0)
                        .trailing(true).completable(true)
        );
    }
}
