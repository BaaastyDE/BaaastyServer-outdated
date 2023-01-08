package de.baaasty.baaastyserver.database;

import de.chojo.sadu.base.QueryFactory;
import de.chojo.sadu.databases.MariaDb;
import de.chojo.sadu.updater.SqlUpdater;

import java.io.IOException;
import java.sql.SQLException;

public class Updater extends QueryFactory {
    public Updater(DatabaseConnection databaseConnection) {
        super(databaseConnection.dataSource());

        try {
            SqlUpdater.builder(databaseConnection.dataSource(), MariaDb.get()).execute();
        } catch (IOException | SQLException exception) {
            exception.printStackTrace();
        }
    }
}
