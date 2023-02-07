package de.baaasty.baaastyserver.database.dao.user.punishments;

import com.fasterxml.jackson.annotation.JsonGetter;
import de.baaasty.baaastyserver.database.dao.User;
import de.baaasty.baaastyserver.database.dao.user.punishments.info.Kick;
import de.chojo.sadu.base.QueryFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Kicks extends QueryFactory {
    private final User user;
    private List<Kick> kicks = null;
    private Kick latest = null;

    public Kicks(User user) {
        super(user);
        this.user = user;
    }

    @JsonGetter
    public List<Kick> all() {
        if (kicks == null)
            this.kicks = builder(Kick.class)
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
                                kick
                            WHERE
                                user_uuid = ?""")
                    .parameter(paramBuilder -> paramBuilder
                            .setUuidAsBytes(user.uuid())
                    )
                    .readRow(row -> new Kick(
                            row.getInt("id"),
                            row.getUuidFromBytes("user_uuid"),
                            row.getUuidFromBytes("executed_uuid"),
                            row.getUuidFromBytes("canceled_uuid"),
                            row.getByte("reason_id"),
                            row.getTimestamp("canceled_date"),
                            row.getTimestamp("create_date")
                    ))
                    .allSync();

        return kicks;
    }

    public Optional<Kick> latest() {
        if (latest == null)
            builder(Kick.class)
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
                                kick
                            WHERE
                                user_uuid = ?
                              AND
                                canceled_uuid IS NULL
                            ORDER BY until_date DESC
                            LIMIT 1""")
                    .parameter(paramBuilder -> paramBuilder
                            .setUuidAsBytes(user.uuid())
                    )
                    .readRow(row -> new Kick(
                            row.getInt("id"),
                            row.getUuidFromBytes("user_uuid"),
                            row.getUuidFromBytes("executed_uuid"),
                            row.getUuidFromBytes("canceled_uuid"),
                            row.getByte("reason_id"),
                            row.getTimestamp("canceled_date"),
                            row.getTimestamp("create_date")
                    ))
                    .firstSync()
                    .ifPresent(kick -> this.latest = kick);

        return Optional.ofNullable(latest);
    }

    public void add(UUID uuid, byte reasonID) {
        builder()
                .query("""
                        INSERT INTO kick (
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
                .send();

        this.latest = null;
    }
}
