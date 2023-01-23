package de.baaasty.baaastyserver.file.type;

import de.baaasty.baaastyserver.BaaastyServer;
import de.baaasty.baaastyserver.file.FileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.LinkedHashMap;

public class MariaDBFile extends FileRepository {
    public MariaDBFile(File folder) {
        super("MariaDB", folder);
    }

    public void setupDefault() {
        initFile();

        LinkedHashMap<String, Object> mariaDbData = getData();

        mariaDbData.putIfAbsent("host", "locahost");
        mariaDbData.putIfAbsent("port", 3306);
        mariaDbData.putIfAbsent("database", "baaastyapi");
        mariaDbData.putIfAbsent("user", "root");
        mariaDbData.putIfAbsent("password", "SecurePassword");
        mariaDbData.putIfAbsent("maximumPoolSize", 10);
        mariaDbData.putIfAbsent("minimumIdle", 2);

        setData(mariaDbData);
        dump();
    }

    public String host() {
        return (String) getData().get("host");
    }

    public int port() {
        return (int) getData().get("port");
    }

    public String database() {
        return (String) getData().get("database");
    }

    public String user() {
        return (String) getData().get("user");
    }

    public String password() {
        return (String) getData().get("password");
    }

    public int maximumPoolSize() {
        return (int) getData().get("maximumPoolSize");
    }

    public int minimumIdle() {
        return (int) getData().get("minimumIdle");
    }
}
