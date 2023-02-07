package de.baaasty.baaastyserver;

import de.baaasty.baaastyserver.database.DatabaseConnection;
import de.baaasty.baaastyserver.database.Updater;
import de.baaasty.baaastyserver.database.access.Transactions;
import de.baaasty.baaastyserver.database.access.Users;
import de.baaasty.baaastyserver.file.type.ConfigFile;
import de.baaasty.baaastyserver.file.type.MariaDBFile;
import de.baaasty.baaastyserver.server.AuthHandler;
import de.baaasty.baaastyserver.server.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class BaaastyServer {
    private static BaaastyServer baaastyServer;
    private final File userDir = new File(System.getProperty("user.dir"));
    private MariaDBFile mariaDBFile;
    private ConfigFile configFile;
    private AuthHandler authHandler;
    private HttpServer server;
    private DatabaseConnection databaseConnection;
    private Users users;
    private Transactions transactions;

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(BaaastyServer.class.getName());

        logger.info("Starting BaaastyServer...");

        baaastyServer = new BaaastyServer();

        baaastyServer.initMariaDBFile();
        baaastyServer.initConfigFile();
        baaastyServer.initDatabaseConnection();
        baaastyServer.initHttpServer();

        logger.info("BaaastyServer started!");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            baaastyServer.server.stop();
            baaastyServer.databaseConnection.disconnect();
        }));
    }

    public void initMariaDBFile() {
        mariaDBFile = new MariaDBFile(userDir);
        mariaDBFile.setupDefault();
    }

    public void initConfigFile() {
        configFile = new ConfigFile(userDir);
        configFile.setupDefault();
    }

    public void initHttpServer() {
        authHandler = new AuthHandler(configFile);
        server = new HttpServer(authHandler);
    }

    public void initDatabaseConnection() {
        databaseConnection = new DatabaseConnection(mariaDBFile);

        new Updater(databaseConnection);

        users = new Users(databaseConnection);
        transactions = new Transactions(databaseConnection);
    }

    public Users users() {
        return users;
    }

    public Transactions transactions() {
        return transactions;
    }

    public static BaaastyServer instance() {
        return baaastyServer;
    }
}
