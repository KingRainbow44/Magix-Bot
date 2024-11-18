package moe.seikimo.magixbot.interactions;

import lombok.experimental.ExtensionMethod;
import moe.seikimo.magixbot.data.annotations.Game;
import moe.seikimo.magixbot.data.models.GameStatistics;
import moe.seikimo.magixbot.extensions.MemberExtensions;
import moe.seikimo.magixbot.extensions.UserExtensions;
import moe.seikimo.magixbot.utils.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.Command.Type;
import tech.xigam.cch.command.Command;
import tech.xigam.cch.utils.Interaction;

import java.time.OffsetDateTime;

@ExtensionMethod({MemberExtensions.class, UserExtensions.class})
public final class GameStatsInteraction extends Command {
    public GameStatsInteraction() {
        super("Fetch Game Statistics",
                "Shows the member's game statistics.",
                InteractionContextType.GUILD,
                InteractionContextType.PRIVATE_CHANNEL);
    }

    @Override
    public void execute(Interaction interaction) {
        interaction.deferReply();

        var name = "Unknown";
        var gameStats = new GameStatistics();
        var isLifetime = interaction.isFromGuild();

        if (interaction.isFromGuild()) {
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

            // Resolve data.
            name = member.getEffectiveName();
            gameStats = member.getStats();
        } else {
            var target = interaction.getTarget();
            if (target == null) {
                interaction.reply(EmbedUtils.error("Invalid target."));
                return;
            }

            // Resolve name.
            name = target.getEffectiveName();

            // Get the user model.
            var data = target.getData();

            // Accumulate game stats.
            for (var guildMemberData : data.getMemberData()) {
                var stats = guildMemberData.getGameStats();
                gameStats.add(stats);
            }
        }

        var asker = interaction.getUser();
        var embed = new EmbedBuilder()
                .setColor(EmbedUtils.getColor())
                .setTimestamp(OffsetDateTime.now())
                .setFooter(
                        "Requested by " + asker.getEffectiveName(),
                        asker.getEffectiveAvatarUrl())
                .setTitle(name + (isLifetime ? "'s Lifetime Stats" : "'s Guild Stats"));

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
