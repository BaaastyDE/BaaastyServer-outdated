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
    private List<Mute> mutes = null;
    private Mute latest = null;

    public Mutes(User user) {
        super(user);
        this.user = user;
    }

    @JsonGetter
    public List<Mute> all() {
        if (mutes == null)
            this.mutes = builder(Mute.class)
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

        return mutes;
    }

    public Optional<Mute> latest() {
        if (latest == null)
            builder(Mute.class)
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
                    .firstSync()
                    .ifPresent(mute -> this.latest = mute);

        return Optional.ofNullable(latest);
    }

    public void add(UUID uuid, byte reasonID, long duration) {
        builder()
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
                .send();

        this.latest = null;
    }
}
