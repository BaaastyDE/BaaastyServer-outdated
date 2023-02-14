package de.baaasty.baaastyserver.file.type;

import de.baaasty.baaastyserver.file.FileRepository;

import java.io.File;
import java.util.LinkedHashMap;

public class MariaDBFile extends FileRepository {
    public MariaDBFile(File folder) {
        super("MariaDB", folder);
    }

    public void setupDefault() {
        initFile();

        LinkedHashMap<String, Object> mariaDbData = data();

        mariaDbData.putIfAbsent("host", "locahost");
        mariaDbData.putIfAbsent("port", 3306);
        mariaDbData.putIfAbsent("database", "baaastyapi");
        mariaDbData.putIfAbsent("user", "root");
        mariaDbData.putIfAbsent("password", "SecurePassword");
        mariaDbData.putIfAbsent("maximumPoolSize", 10);
        mariaDbData.putIfAbsent("minimumIdle", 2);

        data(mariaDbData);
        dump();
    }

    public String host() {
        return (String) data().get("host");
    }

    public int port() {
        return (int) data().get("port");
    }

    public String database() {
        return (String) data().get("database");
    }

    public String user() {
        return (String) data().get("user");
    }

    public String password() {
        return (String) data().get("password");
    }

    public int maximumPoolSize() {
        return (int) data().get("maximumPoolSize");
    }

    public int minimumIdle() {
        return (int) data().get("minimumIdle");
    }
}
