package moe.seikimo.magixbot.data;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.h2.H2Backend;
import lombok.Getter;
import moe.seikimo.magixbot.Config;
import moe.seikimo.magixbot.objects.Initializer;

public final class DatabaseManager implements Initializer {
    @Getter private static DatabaseManager instance;

    @Getter private final MongoServer server;

    public DatabaseManager() {
        if (DatabaseManager.instance != null)
            throw new RuntimeException("DatabaseManager is already initialized.");

        DatabaseManager.instance = this;

        this.server = new MongoServer(new H2Backend("database.db"));
        this.server.bind("127.0.0.1", Config.get().getDatabase().port());
    }

    @Override
    public void initialize() {

    }
}
