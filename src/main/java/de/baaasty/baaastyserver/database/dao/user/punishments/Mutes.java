package de.baaasty.baaastyserver.database.dao.user.punishments;

import com.fasterxml.jackson.annotation.JsonGetter;
import de.baaasty.baaastyserver.database.dao.User;
import de.baaasty.baaastyserver.database.dao.user.punishments.info.Mute;
import de.chojo.sadu.base.QueryFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Mutes extends QueryFactory {
    private final User user;

    public Mutes(User user) {
        super(user);
        this.user = user;
    }

    @JsonGetter
    public List<Mute> all() {
        return builder(Mute.class)
                .query("""
                        SELECT
                            id,
                            user_uuid,
                            executed_uuid,
                            canceled_uuid,
                            reason_id,
                            until_date,
                            canceled_date,
                            create_date
                        FROM
                            mute
                        WHERE
                            user_uuid = ?""")
                .parameter(paramBuilder -> paramBuilder
                        .setUuidAsBytes(user.uuid())
                )
                .readRow(row -> new Mute(
                        row.getInt("id"),
                        row.getUuidFromBytes("user_uuid"),
                        row.getUuidFromBytes("executed_uuid"),
                        row.getUuidFromBytes("canceled_uuid"),
                        row.getByte("reason_id"),
                        row.getTimestamp("until_date"),
                        row.getTimestamp("canceled_date"),
                        row.getTimestamp("create_date")
                ))
                .allSync();
    }

    public Optional<Mute> latest() {
        return builder(Mute.class)
                .query("""
                        SELECT
                            id,
                            user_uuid,
                            executed_uuid,
                            canceled_uuid,
                            reason_id,
                            until_date,
                            canceled_date,
                            create_date
                        FROM
                            mute
                        WHERE
                            user_uuid = ?
                          AND
                            canceled_uuid IS NULL
                        ORDER BY until_date DESC
                        LIMIT 1""")
                .parameter(paramBuilder -> paramBuilder
                        .setUuidAsBytes(user.uuid())
                )
                .readRow(row -> new Mute(
                        row.getInt("id"),
                        row.getUuidFromBytes("user_uuid"),
                        row.getUuidFromBytes("executed_uuid"),
                        row.getUuidFromBytes("canceled_uuid"),
                        row.getByte("reason_id"),
                        row.getTimestamp("until_date"),
                        row.getTimestamp("canceled_date"),
                        row.getTimestamp("create_date")
                ))
                .firstSync();
    }

    public boolean add(UUID uuid, byte reasonID, long duration) {
        return builder()
                .query("""
                        INSERT INTO mute (
                            user_uuid,
                            executed_uuid,
                            reason_id,
                            until_date
                          ) VALUES (
                            ?,
                            ?,
                            ?,
                            DATE_ADD(CURRENT_TIMESTAMP, INTERVAL ? MINUTE)
                        )""")
                .parameter(statement -> statement
                        .setUuidAsBytes(user.uuid())
                        .setUuidAsBytes(uuid)
                        .setByte(reasonID)
                        .setLong(duration)
                )
                .insert()
                .sendSync()
                .changed();
    }
}
