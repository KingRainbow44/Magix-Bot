package moe.seikimo.magixbot.commands;

import moe.seikimo.magixbot.commands.clip.DurationSubCommand;
import moe.seikimo.magixbot.commands.clip.RecordSubCommand;
import moe.seikimo.magixbot.commands.clip.StartSubCommand;
import moe.seikimo.magixbot.commands.clip.StopSubCommand;
import moe.seikimo.magixbot.utils.EmbedUtils;
import tech.xigam.cch.command.Command;
import tech.xigam.cch.command.modifiers.Baseless;
import tech.xigam.cch.command.modifiers.Limited;
import tech.xigam.cch.utils.Interaction;

public final class ClipCommand extends Command implements Baseless, Limited {
    public ClipCommand() {
        super("clip", "Record audio from a voice channel.");

        this.registerSubCommand(new StartSubCommand());
        this.registerSubCommand(new StopSubCommand());
        this.registerSubCommand(new DurationSubCommand());
        this.registerSubCommand(new RecordSubCommand());
    }

    @Override
    public void execute(Interaction interaction) {
        interaction.reply(EmbedUtils.info("Usage: `clip <start|stop|duration|record> [duration]`"));
    }

    @Override
    public boolean isGuildOnly() {
        return true;
    }
}
