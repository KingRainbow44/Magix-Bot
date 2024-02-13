package moe.seikimo.magixbot.commands;

import moe.seikimo.magixbot.Config;
import moe.seikimo.magixbot.utils.EmbedUtils;
import net.dv8tion.jda.api.entities.MessageEmbed;
import tech.xigam.cch.utils.Interaction;

public final class DeployCommand extends tech.xigam.cch.defaults.DeployCommand {
    @Override
    protected boolean permissionCheck(Interaction interaction) {
        return interaction.getUser().getId()
                .equals(Config.get().getBot().ownerId());
    }

    @Override
    protected MessageEmbed embedify(String text) {
        return EmbedUtils.info(text);
    }
}
