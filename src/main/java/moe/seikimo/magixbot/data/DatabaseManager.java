package moe.seikimo.magixbot.data;

import com.mongodb.client.MongoClients;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.h2.H2Backend;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import lombok.Getter;
import moe.seikimo.data.DatabaseUtils;
import moe.seikimo.magixbot.Config;
import org.bson.UuidRepresentation;

import java.util.List;

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

        this.server = new MongoServer(new H2Backend("database.db"));
        this.server.bind("127.0.0.1", Config.get().getDatabase().port());

        // Create a morphia datastore.
        var client = MongoClients.create(this.server.getConnectionString());
        this.datastore = Morphia.createDatastore(client);
        DatabaseUtils.DATASTORE.set(this.datastore);

        // Map known database models.
        this.datastore.getMapper().getConfig()
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .packages(List.of("moe.seikimo.data.models"));
    }
}
