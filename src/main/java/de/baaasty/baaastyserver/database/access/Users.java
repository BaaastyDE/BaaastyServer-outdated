package de.baaasty.baaastyserver.database.access;

import de.baaasty.baaastyserver.database.DatabaseConnection;
import de.baaasty.baaastyserver.database.dao.User;
import de.chojo.sadu.base.QueryFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

/**
 * Class for accessing a user by its uuid or name
 *
 * @author Baaasty
 */
public class Users extends QueryFactory {
    private static final HashMap<UUID, User> users = new HashMap<>();

    public Users(DatabaseConnection databaseConnection) {
        super(databaseConnection.dataSource());
    }

    /**
     * Get the user by the user uuid
     *
     * @param uuid uuid of the user
     * @return user or null if no user is present
     */
    public User byUUID(UUID uuid) {
        if (users.containsKey(uuid)) {
            return users.get(uuid);
        } else {
            return builder(User.class)
                    .query("""
                        SELECT
                            uuid,
                            name,
                            discord_id
                        FROM
                        	user
                        WHERE
                            uuid = ?""")
                    .parameter(paramBuilder -> paramBuilder.setUuidAsBytes(uuid))
                    .readRow(row -> new User(
                            row.getUuidFromBytes("uuid"),
                            row.getString("name"),
                            row.getLong("discord_id"),
                            this
                    ))
                    .firstSync()
                    .orElseGet(() -> new User(uuid, null, null, this));
        }
    }

    /**
     * Remove the user by the uuid from the cache
     *
     * @param uuid uuid of the user
     */
    public void removeUserFromCache(UUID uuid) {
        users.remove(uuid);
    }

    /**
     * sex? ja baby uwu :)
     * Get the user by the uuid and add him to the cache
     *
     * @param uuid uuid of the user
     */
    public void addUserToCache(UUID uuid) {
        users.put(uuid, byUUID(uuid));
    }

    /**
     * Get the user by the username
     *
     * @param name name of the user
     * @return user or null if no user is present
     */
    public Optional<User> byName(String name) {
        return builder(User.class)
                .query("""
                        SELECT
                        	uuid,
                        	name,
                        	discord_id
                        FROM
                        	user
                        WHERE
                            name = ?""")
                .parameter(paramBuilder -> paramBuilder.setString(name))
                .readRow(row -> new User(
                        row.getUuidFromBytes("uuid"),
                        row.getString("name"),
                        row.getLong("discord_id"),
                        this
                ))
                .firstSync();
    }
}
