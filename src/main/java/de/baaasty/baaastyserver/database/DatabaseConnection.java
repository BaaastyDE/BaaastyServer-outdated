package de.baaasty.baaastyserver.database;

import com.zaxxer.hikari.HikariDataSource;
import de.chojo.sadu.databases.MariaDb;
import de.chojo.sadu.datasource.DataSourceCreator;

/**
 * Class to build a data source with host, port, database, username and password from system env
 */
public class DatabaseConnection {
    private final HikariDataSource dataSource;

    public DatabaseConnection() {
        dataSource = DataSourceCreator.create(MariaDb.get())
                .configure(config -> config
                        .host(System.getenv("MARIADB_HOST"))
                        .port(System.getenv("MARIADB_PORT"))
                        .database(System.getenv("MARIADB_DATABASE"))
                        .user(System.getenv("MARIADB_USER"))
                        .password(System.getenv("MARIADB_PASSWORD"))
                )
                .create()
                .withMaximumPoolSize(Integer.parseInt(System.getenv("MARIADB_MAXPOOLSIZE")))
                .withMinimumIdle(Integer.parseInt(System.getenv("MARIADB_MINIDLE")))
                .build();
    }

    public void disconnect() {
        if (!dataSource.isClosed()) dataSource.close();
    }

    public HikariDataSource dataSource() {
        return dataSource;
    }
}