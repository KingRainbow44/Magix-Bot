package moe.seikimo.magixbot.interactions;

import lombok.experimental.ExtensionMethod;
import moe.seikimo.magixbot.extensions.GuildExtensions;
import moe.seikimo.magixbot.utils.EmbedUtils;
import moe.seikimo.magixbot.utils.JDAUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.Command.Type;
import tech.xigam.cch.command.Command;
import tech.xigam.cch.utils.Interaction;

import java.time.OffsetDateTime;

@ExtensionMethod(GuildExtensions.class)
public final class StarInteraction extends Command {
    public StarInteraction() {
        super("Star Message", "Adds a message to the starboard.");
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

        // Check if starboard channel exists.
        var channel = guild.getData().getStarboardChannel();
        if (channel == null) {
            interaction.reply(EmbedUtils.error("Starboard channel not set."));
            return;
        }

        var message = interaction.getMessage();
        if (message == null) {
            interaction.reply(EmbedUtils.error("Invalid message."));
            return;
        }

        var sender = message.getAuthor();
        if (sender.isBot() || sender.isSystem()) {
            interaction.reply(EmbedUtils.error("You can't star non-user messages!"));
            return;
        }

        var embed = new EmbedBuilder()
                .setTitle("Starred by " + interaction.getUser().getEffectiveName())
                .setDescription(message.getContentRaw())
                .addField("Original Message", message.getJumpUrl(), false)
                .setTimestamp(OffsetDateTime.now())
                .setFooter(sender.getEffectiveName(), sender.getEffectiveAvatarUrl())
                .setColor(EmbedUtils.getColor());

        var attachments = message.getAttachments();
        if (!attachments.isEmpty()) {
            var attachment = attachments.get(0);
            if (attachment.isImage()) {
                embed.setImage(attachment.getUrl());
            }
        }

        channel.sendMessageEmbeds(embed.build()).queue(newMessage ->
                interaction.reply(EmbedUtils.info("Message starred at " + newMessage.getJumpUrl())));
    }

    @Override
    public Type commandType() {
        return Type.MESSAGE;
    }
}
