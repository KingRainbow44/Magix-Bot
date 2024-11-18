package moe.seikimo.magixbot.commands.clip;

import lombok.experimental.ExtensionMethod;
import moe.seikimo.magixbot.extensions.GuildExtensions;
import moe.seikimo.magixbot.utils.EmbedUtils;
import moe.seikimo.magixbot.utils.JDAUtils;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import tech.xigam.cch.command.SubCommand;
import tech.xigam.cch.command.modifiers.Arguments;
import tech.xigam.cch.utils.Argument;
import tech.xigam.cch.utils.Interaction;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@ExtensionMethod(GuildExtensions.class)
public final class DurationSubCommand extends SubCommand implements Arguments {
    public DurationSubCommand() {
        super("duration", "Sets the audio recording duration.");
    }

    @Override
    public void execute(Interaction interaction) {
        if (!JDAUtils.isNotFromGuild(interaction)) return;

        var duration = interaction.getArgument("duration", Long.class);
        if (duration < 0) {
            interaction.reply(EmbedUtils.error("The duration must be a positive number."));
            return;
        }

        interaction.deferReply();

        var guild = Objects.requireNonNull(interaction.getGuild());
        var data = guild.getData();

        // Set the duration.
        data.setClipDuration((int) (long) duration);

        data.save(() -> interaction.reply(EmbedUtils.info(
                "The audio clip duration has been set to " + duration + " seconds.")));
    }

    @Override
    public Collection<Argument> getArguments() {
        return List.of(
                Argument.create("duration", "The audio clip duration.", "duration", OptionType.INTEGER, true, 0)
                        .range(30, 300)
        );
    }
}
