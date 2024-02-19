package moe.seikimo.magixbot;

import ch.qos.logback.classic.Level;
import lombok.Getter;
import moe.seikimo.console.Arguments;
import moe.seikimo.magixbot.data.DatabaseManager;
import moe.seikimo.magixbot.features.game.GameManager;
import moe.seikimo.magixbot.listeners.GameListener;
import moe.seikimo.magixbot.listeners.GenericListener;
import moe.seikimo.magixbot.utils.EmbedUtils;
import moe.seikimo.magixbot.utils.JDAUtils;
import moe.seikimo.magixbot.utils.ReflectionUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.xigam.cch.ComplexCommandHandler;

import java.util.EnumSet;

public final class MagixBot {
    static {
        // Set Logback configuration.
        System.setProperty("logback.configurationFile", "logback.xml");
    }

    @Getter private static final Logger logger
            = LoggerFactory.getLogger("Magix Bot");
    @Getter private static final OkHttpClient httpClient
            = new OkHttpClient();
    @Getter private static MagixBot instance;

    public static void main(String[] args) {
        MagixBot.getLogger().info("Starting Magix bot...");
        MagixBot.instance = new MagixBot(new Arguments(args));
    }

    @Getter private ComplexCommandHandler commandHandler;
    @Getter private JDA botInstance;

    private MagixBot(Arguments arguments) {
        Config.load(arguments.get("config", "config.json"));

        // Check if debug mode is enabled.
        if (Config.get().getLogger().debug()) {
            var logger = (ch.qos.logback.classic.Logger) MagixBot.logger;
            logger.setLevel(Level.DEBUG);

            MagixBot.getLogger().debug("Debug mode is enabled.");
        }

        // Check if the config is valid.
        if (Config.get().getToken().equals("your-token-here")) {
            MagixBot.getLogger().error("Please set your token in the config.");
            return;
        }

        try {
            // Start the bot.
            this.start();

            // Initialize systems.
            DatabaseManager.initialize();
            GameManager.initialize();
        } catch (Exception ex) {
            MagixBot.getLogger().error("Unable to start the bot.", ex);
        }
    }

    /**
     * Creates the bot instance.
     */
    private void start() {
        var config = Config.get();
        var commands = config.getCommands();
        var activity = config.getActivity();

        var builder = JDABuilder.create(config.getToken(),
                EnumSet.allOf(GatewayIntent.class))
                .setHttpClient(MagixBot.getHttpClient())
                .setStatus(OnlineStatus.IDLE)
                .setActivity(JDAUtils.createActivity(
                        activity.activity(), activity.name()))
                .enableCache(CacheFlag.ACTIVITY, CacheFlag.ONLINE_STATUS)
                .addEventListeners(new GenericListener(), new GameListener());

        this.botInstance = builder.build();

        // Enable CCH.
        this.commandHandler = new ComplexCommandHandler(commands.prefixEnabled());
        this.commandHandler.setPrefix(commands.prefix());
        this.commandHandler.setJda(this.botInstance);
        this.commandHandler.mentionDefault = false;
        this.commandHandler.onArgumentError = (interaction) ->
                interaction.reply(EmbedUtils.error("Invalid arguments."));
        this.commandHandler.onContextError = (interaction, error) ->
                interaction.reply(EmbedUtils.error(error.getMessage()));

        // Register commands.
        ReflectionUtils.getAllCommands().forEach(command -> {
            this.commandHandler.registerCommand(command);
            MagixBot.getLogger().debug("Registered command: '{}'", command.getLabel());
        });
    }
}
