package de.baaasty.baaastyserver.database.dao;

import com.fasterxml.jackson.annotation.JsonGetter;
import de.baaasty.baaastyserver.database.access.Users;
import de.baaasty.baaastyserver.database.dao.user.Currencies;
import de.baaasty.baaastyserver.database.dao.user.Meta;
import de.baaasty.baaastyserver.database.dao.user.Punishments;
import de.baaasty.baaastyserver.database.access.Transactions;
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
    private final Currencies currencies;
    private final Punishments punishments;

    public User(UUID uuid, String name, Long discordId, Users users) {
        super(users);
        this.users = users;
        this.uuid = uuid;
        this.name = name;
        this.discordId = discordId;
        this.meta = new Meta(this);
        this.currencies = new Currencies(this);
        this.punishments = new Punishments(this);
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
    public void name(String name) {
        builder()
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
                .send();

        this.name = name;
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
    public boolean discordId(long discordId) {
        this.discordId = discordId;

        return true;
    }

    /**
     * Delete the user from database. If changed, remove it from cache.
     */
    public void delete() {
        builder()
                .query("""
                        DELETE FROM
                            user
                        WHERE
                            uuid = ?""")
                .parameter(paramBuilder -> paramBuilder.setUuidAsBytes(uuid))
                .delete()
                .send();

        upload();
        users.removeUserFromCache(uuid);
    }

    /**
     * Upload the user to the database.
     */
    public void upload() {
        builder()
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
                .send();
    }

    public Meta meta() {
        return meta;
    }

    public Currencies currencies() {
        return currencies;
    }

    public Punishments punishments() {
        return punishments;
    }
}