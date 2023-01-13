package de.baaasty.baaastyserver.database.dao;

import com.fasterxml.jackson.annotation.*;
import de.baaasty.baaastyserver.database.access.Users;
import de.baaasty.baaastyserver.database.dao.user.Meta;
import de.chojo.sadu.base.QueryFactory;

import java.util.UUID;

/**
 * User class to access the information, settings etc. about the player/user
 */
public class User extends QueryFactory {
    private final Users users;
    private final UUID uuid;
    private String name;
    private Long discordId;
    private final Meta meta;

    public User(UUID uuid, String name, Long discordId, Users users) {
        super(users);
        this.users = users;
        this.uuid = uuid;
        this.name = name;
        this.discordId = discordId;
        this.meta = new Meta(this);
    }

    /**
     * Get the uuid of the user
     *
     * @return The uuid of the user
     */
    @JsonGetter
    public UUID uuid() {
        return uuid;
    }

    /**
     * Get the name of the user
     *
     * @return The name of the user
     */
    @JsonGetter
    public String name() {
        return name;
    }

    /**
     * Set a new name of the user
     *
     * @param name The new name of the user
     */
    public boolean name(String name) {
        boolean changed = builder()
                .query("""
                        INSERT INTO user (
                            uuid,
                            name
                        ) VALUES (
                            ?,
                            ?
                        )
                        ON DUPLICATE KEY
                        UPDATE
                            name = ?""")
                .parameter(paramBuilder -> paramBuilder
                        .setUuidAsBytes(uuid)
                        .setString(name)
                        .setString(name))
                .update()
                .sendSync()
                .changed();
        if (changed)
            this.name = name;

        return changed;
    }

    /**
     * Get the discord id the user
     *
     * @return The name of the user
     */
    @JsonGetter
    public Long discordId() {
        return discordId;
    }

    /**
     * Set a new discord id of the user
     *
     * @param discordId The new discord id of the user
     */
    public void discordId(long discordId) {
        this.discordId = discordId;
    }

    /**
     * Delete the user from database. If changed, remove it from cache.
     *
     * @return If changed true, if not false
     */
    public boolean delete() {
        boolean changed = builder()
                .query("""
                        DELETE FROM
                            user
                        WHERE
                            uuid = ?""")
                .parameter(paramBuilder -> paramBuilder.setUuidAsBytes(uuid))
                .delete()
                .sendSync()
                .changed();

        if (changed) users.removeUserCache(uuid);

        return changed;
    }

    /**
     * Upload the user to the database.
     *
     * @return If changed true, if not false
     */
    public boolean upload() {
        return builder()
                .query("""
                        INSERT INTO user (
                            uuid,
                            name,
                            discord_id
                        ) VALUES (
                            ?,
                            ?,
                            ?
                        )
                        ON DUPLICATE KEY
                        UPDATE
                            name = ?,
                            discord_id = ?""")
                .parameter(paramBuilder -> paramBuilder
                        .setUuidAsBytes(uuid)
                        .setString(name)
                        .setLong(discordId)
                        .setString(name)
                        .setLong(discordId))
                .update()
                .sendSync()
                .changed();
    }

    public Meta meta() {
        return meta;
    }
}