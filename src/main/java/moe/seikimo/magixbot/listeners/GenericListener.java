package moe.seikimo.magixbot.listeners;

import moe.seikimo.magixbot.MagixBot;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public final class GenericListener extends ListenerAdapter {
    @Override
    public void onReady(@NotNull ReadyEvent event) {
        MagixBot.getLogger().info("Bot is ready!");
    }
}
