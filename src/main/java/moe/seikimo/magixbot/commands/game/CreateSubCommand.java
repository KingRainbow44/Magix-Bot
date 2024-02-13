package moe.seikimo.magixbot.commands.game;

import moe.seikimo.magixbot.features.game.GameManager;
import moe.seikimo.magixbot.utils.EmbedUtils;
import moe.seikimo.magixbot.utils.GameUtils;
import moe.seikimo.magixbot.utils.JDAUtils;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import tech.xigam.cch.command.SubCommand;
import tech.xigam.cch.command.modifiers.Arguments;
import tech.xigam.cch.command.modifiers.Completable;
import tech.xigam.cch.utils.Argument;
import tech.xigam.cch.utils.Completion;
import tech.xigam.cch.utils.Interaction;

import java.util.Collection;
import java.util.List;

public final class CreateSubCommand extends SubCommand implements Completable, Arguments {
    public CreateSubCommand() {
        super("create", "Creates an instance of a new game.");
    }

    @Override
    public void execute(Interaction interaction) {
        if (!JDAUtils.isNotFromGuild(interaction)) return;

        // Check if the user is already in a game.
        var guild = interaction.getGuild();
        if (GameManager.getRunning().containsKey(guild)) {
            interaction.reply(EmbedUtils.error("The guild already has a game running."));
            return;
        }

        var member = interaction.getMember();
        if (GameUtils.isInGame(member)) {
            interaction.reply(EmbedUtils.error("You are already in a game."));
            return;
        }

        var type = interaction.getArgument("type", String.class);
        var game = GameManager.createGame(type, interaction);
        if (game == null) {
            interaction.reply(EmbedUtils.error("The game type you specified does not exist."));
        } else {
            game.addPlayer(member);
            interaction.reply(EmbedUtils.info("The game has been created."));
        }
    }

    @Override
    public void complete(Completion completion) {
        var input = completion.getInput().toLowerCase();

        // Get similar options.
        var other = GameManager.getAvailableGames();
        for (var game : other) {
            if (game.startsWith(input)) {
                completion.addChoice(game, game);
            }
        }

        completion.reply();
    }

    @Override
    public Collection<Argument> getArguments() {
        return List.of(
                Argument.create("type", "The type of game you want to create.", "type", OptionType.STRING, true, 0)
                        .trailing(true).completable(true)
        );
    }
}
