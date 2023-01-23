package de.baaasty.baaastyserver.database.dao.user.punishments;

import com.fasterxml.jackson.annotation.JsonGetter;
import de.baaasty.baaastyserver.database.dao.User;
import de.baaasty.baaastyserver.database.dao.user.punishments.info.Warn;
import de.chojo.sadu.base.QueryFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Warns extends QueryFactory {
    private final User user;

    public Warns(User user) {
        super(user);
        this.user = user;
    }

    @JsonGetter
    public List<Warn> all() {
        return builder(Warn.class)
                .query("""
                        SELECT
                            id,
                            user_uuid,
                            executed_uuid,
                            canceled_uuid,
                            reason_id,
                            canceled_date,
                            create_date
                        FROM
                            warn
                        WHERE
                            user_uuid = ?""")
                .parameter(paramBuilder -> paramBuilder
                        .setUuidAsBytes(user.uuid())
                )
                .readRow(row -> new Warn(
                        row.getInt("id"),
                        row.getUuidFromBytes("user_uuid"),
                        row.getUuidFromBytes("executed_uuid"),
                        row.getUuidFromBytes("canceled_uuid"),
                        row.getByte("reason_id"),
                        row.getTimestamp("canceled_date"),
                        row.getTimestamp("create_date")
                ))
                .allSync();
    }

    public Optional<Warn> latest() {
        return builder(Warn.class)
                .query("""
                        SELECT
                            id,
                            user_uuid,
                            executed_uuid,
                            canceled_uuid,
                            reason_id,
                            canceled_date,
                            create_date
                        FROM
                            warn
                        WHERE
                            user_uuid = ?
                          AND
                            canceled_uuid IS NULL
                        ORDER BY until_date DESC
                        LIMIT 1""")
                .parameter(paramBuilder -> paramBuilder
                        .setUuidAsBytes(user.uuid())
                )
                .readRow(row -> new Warn(
                        row.getInt("id"),
                        row.getUuidFromBytes("user_uuid"),
                        row.getUuidFromBytes("executed_uuid"),
                        row.getUuidFromBytes("canceled_uuid"),
                        row.getByte("reason_id"),
                        row.getTimestamp("canceled_date"),
                        row.getTimestamp("create_date")
                ))
                .firstSync();
    }

    public boolean add(UUID uuid, byte reasonID) {
        return builder()
                .query("""
                        INSERT INTO warn (
                            user_uuid,
                            executed_uuid,
                            reason_id
                          ) VALUES (
                            ?,
                            ?,
                            ?
                        )""")
                .parameter(statement -> statement
                        .setUuidAsBytes(user.uuid())
                        .setUuidAsBytes(uuid)
                        .setByte(reasonID)
                )
                .insert()
                .sendSync()
                .changed();
    }
}
