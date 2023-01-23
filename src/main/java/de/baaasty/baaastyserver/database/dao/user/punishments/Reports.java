package de.baaasty.baaastyserver.database.dao.user.punishments;

import com.fasterxml.jackson.annotation.JsonGetter;
import de.baaasty.baaastyserver.database.dao.User;
import de.baaasty.baaastyserver.database.dao.user.punishments.info.Report;
import de.chojo.sadu.base.QueryFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Reports extends QueryFactory {
    private final User user;

    public Reports(User user) {
        super(user);
        this.user = user;
    }

    @JsonGetter
    public List<Report> all() {
        return builder(Report.class)
                .query("""
                        SELECT
                            id,
                            user_uuid,
                            executed_uuid,
                            claimed_uuid,
                            reason_id,
                            claimed_date,
                            create_date
                        FROM
                            report
                        WHERE
                            user_uuid = ?""")
                .parameter(paramBuilder -> paramBuilder
                        .setUuidAsBytes(user.uuid())
                )
                .readRow(row -> new Report(
                        row.getInt("id"),
                        row.getUuidFromBytes("user_uuid"),
                        row.getUuidFromBytes("executed_uuid"),
                        row.getUuidFromBytes("claimed_uuid"),
                        row.getByte("reason_id"),
                        row.getTimestamp("claimed_date"),
                        row.getTimestamp("create_date")
                ))
                .allSync();
    }

    public Optional<Report> latest() {
        return builder(Report.class)
                .query("""
                        SELECT
                            id,
                            user_uuid,
                            executed_uuid,
                            claimed_uuid,
                            reason_id,
                            claimed_date,
                            create_date
                        FROM
                            report
                        WHERE
                            user_uuid = ?
                          AND
                            claimed_uuid IS NULL
                        ORDER BY until_date DESC
                        LIMIT 1""")
                .parameter(paramBuilder -> paramBuilder
                        .setUuidAsBytes(user.uuid())
                )
                .readRow(row -> new Report(
                        row.getInt("id"),
                        row.getUuidFromBytes("user_uuid"),
                        row.getUuidFromBytes("executed_uuid"),
                        row.getUuidFromBytes("claimed_uuid"),
                        row.getByte("reason_id"),
                        row.getTimestamp("claimed_date"),
                        row.getTimestamp("create_date")
                ))
                .firstSync();
    }

    public boolean add(UUID uuid, byte reasonID) {
        return builder()
                .query("""
                        INSERT INTO report (
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
