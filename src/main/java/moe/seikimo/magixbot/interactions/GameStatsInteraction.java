package moe.seikimo.magixbot.interactions;

import moe.seikimo.magixbot.data.annotations.Game;
import moe.seikimo.magixbot.data.models.GameStatistics;
import moe.seikimo.magixbot.utils.EmbedUtils;
import moe.seikimo.magixbot.utils.JDAUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.Command.Type;
import tech.xigam.cch.command.Command;
import tech.xigam.cch.utils.Interaction;

import java.time.OffsetDateTime;

public final class GameStatsInteraction extends Command {
    public GameStatsInteraction() {
        super("Fetch Game Statistics",
                "Shows the member's game statistics.");
    }

    @Override
    public void execute(Interaction interaction) {
        if (!JDAUtils.isNotFromGuild(interaction)) return;

        interaction.deferReply();

        var guild = interaction.getGuild();
        if (guild == null) {
            interaction.reply(EmbedUtils.error("Invalid guild."));
            return;
        }

        var target = interaction.getTarget();
        if (target == null) {
            interaction.reply(EmbedUtils.error("Invalid target."));
            return;
        }

        var member = guild.getMember(target);
        if (member == null) {
            interaction.reply(EmbedUtils.error("Invalid member."));
            return;
        }

        var asker = interaction.getUser();
        var gameStats = member.getStats();
        var embed = new EmbedBuilder()
                .setColor(EmbedUtils.getColor())
                .setTimestamp(OffsetDateTime.now())
                .setFooter(
                        "Requested by " + asker.getEffectiveName(),
                        asker.getEffectiveAvatarUrl())
                .setTitle(member.getEffectiveName() + "'s Game Stats");

        // Add game stats to the embed.
        var fields = GameStatistics.class.getDeclaredFields();
        for (var field : fields) try {
            field.setAccessible(true);

            var nameData = field.getAnnotation(Game.class);
            if (nameData == null) continue;

            var fieldValue = field.get(gameStats);

            // Add the field to the embed.
            embed.addField(nameData.name(),
                    fieldValue.toString() + " " + nameData.description(),
                    true);

            field.setAccessible(false);
        } catch (Exception ignored) { }

        // Send the embed.
        interaction.reply(embed.build());
    }

    @Override
    public Type commandType() {
        return Type.USER;
    }
}
