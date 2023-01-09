package de.baaasty.baaastyserver;

import de.baaasty.baaastyserver.database.DatabaseConnection;
import de.baaasty.baaastyserver.database.Updater;
import de.baaasty.baaastyserver.database.access.Users;
import de.baaasty.baaastyserver.server.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaaastyServer {
    private static BaaastyServer baaastyServer;
    private static final Logger logger = LoggerFactory.getLogger(BaaastyServer.class.getName());
    private HttpServer server;
    private DatabaseConnection databaseConnection;
    private Users users;

    public static void main(String[] args) {
        logger.info("Starting BaaastyServer...");

        baaastyServer = new BaaastyServer();

        baaastyServer.initDatabaseConnection();
        baaastyServer.initHttpServer();

        logger.info("BaaastyServer started!");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            baaastyServer.server.stop();
            baaastyServer.databaseConnection.disconnect();
        }));
    }

    public void initHttpServer() {
        server = new HttpServer();
    }

    public void initDatabaseConnection() {
        databaseConnection = new DatabaseConnection();

        new Updater(databaseConnection);

        users = new Users(databaseConnection);
    }

    public Users users() {
        return users;
    }

    public static BaaastyServer instance() {
        return baaastyServer;
    }
}
