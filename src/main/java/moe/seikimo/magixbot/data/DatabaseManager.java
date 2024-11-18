package moe.seikimo.magixbot.data;

import com.mongodb.client.MongoClients;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.h2.H2Backend;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import lombok.Getter;
import moe.seikimo.data.DatabaseUtils;
import moe.seikimo.magixbot.Config;

public final class DatabaseManager {
    @Getter private static DatabaseManager instance;

    /**
     * Initializes the database manager.
     */
    public static void initialize() {
        if (DatabaseManager.instance == null)
            DatabaseManager.instance = new DatabaseManager();
    }

    @Getter private final MongoServer server;
    @Getter private final Datastore datastore;

    public DatabaseManager() {
        if (DatabaseManager.instance != null)
            throw new RuntimeException("DatabaseManager is already initialized.");

        var config = Config.get().getDatabase();
        if (config.useLocal()) {
            this.server = new MongoServer(new H2Backend("database.db"));
            this.server.bind("127.0.0.1", config.port());
        } else {
            this.server = null;
        }

        // Create a morphia datastore.
        var client = MongoClients.create(config.useLocal() ?
                this.server.getConnectionString() : config.uri());
        this.datastore = Morphia.createDatastore(client);
        DatabaseUtils.DATASTORE.set(this.datastore);
    }
}
