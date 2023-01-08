package de.baaasty.baaastyserver;

import de.baaasty.baaastyserver.database.DatabaseConnection;
import de.baaasty.baaastyserver.database.Updater;
import de.baaasty.baaastyserver.database.access.Users;
import de.baaasty.baaastyserver.server.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaaastyServer {
    private static BaaastyServer baaastyServer;
    private static Logger logger = LoggerFactory.getLogger(BaaastyServer.class.getName());
    private HttpServer server;
    private DatabaseConnection databaseConnection;
    private Users users;

    public static void main(String[] args) {
        logger.info("Starting BaaastyServer...");

        baaastyServer = new BaaastyServer();

        baaastyServer.initDatabaseConnection();
        baaastyServer.initHttpServer();

        Runtime runtime = Runtime.getRuntime();
        runtime.addShutdownHook(new Thread(baaastyServer.server::stop));
        runtime.addShutdownHook(new Thread(baaastyServer.databaseConnection::disconnect));
    }

    public void initHttpServer() {
        server = new HttpServer();
        new Thread(server).start();
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
