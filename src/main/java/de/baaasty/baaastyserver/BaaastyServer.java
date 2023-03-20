package de.baaasty.baaastyserver;

import de.baaasty.baaastyserver.database.DatabaseConnection;
import de.baaasty.baaastyserver.database.Updater;
import de.baaasty.baaastyserver.database.access.Transactions;
import de.baaasty.baaastyserver.database.access.Users;
import de.baaasty.baaastyserver.file.type.ConfigFile;
import de.baaasty.baaastyserver.file.type.MariaDBFile;
import de.baaasty.baaastyserver.http.auth.AuthHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class BaaastyServer {
    private static BaaastyServer baaastyServer;
    private final File userDir = new File(System.getProperty("user.dir"));
    private MariaDBFile mariaDBFile;
    private ConfigFile configFile;
    private AuthHandler authHandler;
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
        baaastyServer.initAuthHandler();

        SpringApplication.run(BaaastyServer.class);

        logger.info("BaaastyServer started!");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            baaastyServer.databaseConnection.disconnect();
            baaastyServer.mariaDBFile.dump();
            baaastyServer.configFile.dump();
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

    public void initDatabaseConnection() {
        databaseConnection = new DatabaseConnection();

        new Updater(databaseConnection);

        users = new Users(databaseConnection);
        transactions = new Transactions(databaseConnection);
    }

    public void initAuthHandler() {
        authHandler = new AuthHandler();
    }

    public AuthHandler authHandler() {
        return authHandler;
    }

    public ConfigFile configFile() {
        return configFile;
    }

    public MariaDBFile mariaDBFile() {
        return mariaDBFile;
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
