package moe.seikimo.magixbot;

import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import moe.seikimo.general.EncodingUtils;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.io.File;
import java.nio.file.Files;

@Data
public final class Config {
    private static Config instance = new Config();

    /**
     * @return The configuration instance.
     */
    public static Config get() {
        return Config.instance;
    }

    /**
     * Loads the configuration from a file.
     */
    @SneakyThrows
    public static void load(String fileName) {
        var configFile = new File(fileName);

        if (!configFile.exists()) {
            // Save this configuration.
            Config.save();
        } else {
            // Load the configuration.
            Config.instance = EncodingUtils.jsonDecode(
                    configFile, Config.class);

            // Check if the configuration is null.
            if (Config.instance == null) {
                Config.instance = new Config();
            }
        }
    }

    /**
     * Saves the configuration.
     */
    @SneakyThrows
    public static void save() {
        var configFile = new File("config.json");

        // Save the configuration.
        var json = EncodingUtils.jsonEncode(Config.instance);
        Files.write(configFile.toPath(), json.getBytes());
    }

    private String token = "your-token-here";
    private Commands commands = new Commands();
    private BotActivity activity = new BotActivity();
    private Database database = new Database();

    @Getter
    @Accessors(fluent = true)
    public static class Commands {
        private boolean enabled = true;
        private String prefix = "m!";
    }

    @Getter
    @Accessors(fluent = true)
    public static class BotActivity {
        private String activity = "playing";
        private String name = "with magic";
    }

    @Getter
    @Accessors(fluent = true)
    public static class Database {
        private int port = 27018;
    }
}
