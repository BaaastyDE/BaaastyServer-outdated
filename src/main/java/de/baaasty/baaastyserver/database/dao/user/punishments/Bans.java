package de.baaasty.baaastyserver.database.dao.user.punishments;

import com.fasterxml.jackson.annotation.JsonGetter;
import de.baaasty.baaastyserver.database.dao.User;
import de.baaasty.baaastyserver.database.dao.user.punishments.info.Ban;
import de.chojo.sadu.base.QueryFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Bans extends QueryFactory {
    private final User user;
    private List<Ban> bans = null;
    private Ban latest = null;

    public Bans(User user) {
        super(user);
        this.user = user;
    }

    @JsonGetter
    public List<Ban> all() {
        if (bans == null)
            this.bans = builder(Ban.class)
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
                                ban
                            WHERE
                                user_uuid = ?""")
                    .parameter(paramBuilder -> paramBuilder
                            .setUuidAsBytes(user.uuid())
                    )
                    .readRow(row -> new Ban(
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

        return bans;
    }

    public Optional<Ban> latest() {
        if (latest == null)
            builder(Ban.class)
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
                                ban
                            WHERE
                                user_uuid = ?
                              AND
                                canceled_uuid IS NULL
                            ORDER BY until_date DESC
                            LIMIT 1""")
                    .parameter(paramBuilder -> paramBuilder
                            .setUuidAsBytes(user.uuid())
                    )
                    .readRow(row -> new Ban(
                            row.getInt("id"),
                            row.getUuidFromBytes("user_uuid"),
                            row.getUuidFromBytes("executed_uuid"),
                            row.getUuidFromBytes("canceled_uuid"),
                            row.getByte("reason_id"),
                            row.getTimestamp("until_date"),
                            row.getTimestamp("canceled_date"),
                            row.getTimestamp("create_date")
                    ))
                    .firstSync()
                    .ifPresent(ban -> this.latest = ban);

        return Optional.ofNullable(latest);
    }

    public void add(UUID uuid, byte reasonID, long duration) {
        builder()
                .query("""
                        INSERT INTO ban (
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
                .send();

        this.latest = null;
    }
}
