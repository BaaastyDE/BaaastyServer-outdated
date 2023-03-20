package de.baaasty.baaastyserver.database;

import com.zaxxer.hikari.HikariDataSource;
import de.baaasty.baaastyserver.BaaastyServer;
import de.baaasty.baaastyserver.file.type.MariaDBFile;
import de.chojo.sadu.databases.MariaDb;
import de.chojo.sadu.datasource.DataSourceCreator;

/**
 * Class to build a data source with host, port, database, username and password from system env
 */
public class DatabaseConnection {
    private final MariaDBFile mariaDBFile = BaaastyServer.instance().mariaDBFile();
    private final HikariDataSource dataSource;

    public DatabaseConnection() {
        dataSource = DataSourceCreator.create(MariaDb.get())
                .configure(config -> config
                        .host(mariaDBFile.host())
                        .port(mariaDBFile.port())
                        .database(mariaDBFile.database())
                        .user(mariaDBFile.user())
                        .password(mariaDBFile.password())
                )
                .create()
                .withMaximumPoolSize(mariaDBFile.maximumPoolSize())
                .withMinimumIdle(mariaDBFile.minimumIdle())
                .build();
    }

    public void disconnect() {
        if (!dataSource.isClosed()) dataSource.close();
    }

    public HikariDataSource dataSource() {
        return dataSource;
    }
}