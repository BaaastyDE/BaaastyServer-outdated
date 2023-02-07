package de.baaasty.baaastyserver.database.access;

import de.baaasty.baaastyserver.database.DatabaseConnection;
import de.baaasty.baaastyserver.database.dao.Transaction;
import de.chojo.sadu.base.QueryFactory;

import javax.annotation.Nullable;
import java.util.UUID;

public class Transactions extends QueryFactory {
    public Transactions(DatabaseConnection databaseConnection) {
        super(databaseConnection.dataSource());
    }

    public Transaction byID (long id) {
        return builder(Transaction.class)
                .query("""
                            SELECT
                                id,
                                user_uuid,
                                target_uuid,
                                amethysts,
                                shards
                            FROM
                                currency_transactions
                            WHERE
                                id = ?""")
                .parameter(paramBuilder -> paramBuilder
                        .setLong(id)
                )
                .readRow(row -> new Transaction(
                        row.getLong("id"),
                        row.getUuidFromBytes("user_uuid"),
                        row.getUuidFromBytes("target_uuid"),
                        row.getLong("amethysts"),
                        row.getLong("shards")
                ))
                .firstSync()
                .orElse(new Transaction(id, null, null, 0, 0));
    }

    public void amethysts(@Nullable UUID uuid, @Nullable UUID targetUuid, long amethysts) {
        builder()
                .query("""
                        INSERT INTO currency_transactions (
                            user_uuid,
                            target_uuid,
                            amethysts
                        ) VALUES (
                            ?,
                            ?,
                            ?
                        )""")
                .parameter(paramBuilder -> paramBuilder
                        .setUuidAsBytes(uuid)
                        .setUuidAsBytes(targetUuid)
                        .setLong(amethysts)
                )
                .update()
                .send();
    }

    public long amethysts(UUID uuid, int hours) {
        long send = builder(Long.class)
                .query("""
                            SELECT
                                SUM(amethysts) AS sumAmethysts
                            FROM
                                currency_transactions
                            WHERE
                                user_uuid = ?
                              AND
                                create_date > DATE_SUB(CURRENT_TIMESTAMP, INTERVAL ? HOUR)
                            GROUP BY user_uuid""")
                .parameter(paramBuilder -> paramBuilder
                        .setUuidAsBytes(uuid)
                        .setInt(hours)
                )
                .readRow(row -> row.getLong("sumAmethysts"))
                .firstSync()
                .orElse(0L);

        long received = builder(Long.class)
                .query("""
                        SELECT
                            SUM(amethysts) AS sumAmethysts
                        FROM
                            currency_transactions
                        WHERE
                            target_uuid = ?
                          AND
                            create_date > DATE_SUB(CURRENT_TIMESTAMP, INTERVAL ? HOUR)
                        GROUP BY target_uuid""")
                .parameter(paramBuilder -> paramBuilder
                        .setUuidAsBytes(uuid)
                        .setInt(hours)
                )
                .readRow(row -> row.getLong("sumAmethysts"))
                .firstSync()
                .orElse(0L);

        return received - send;
    }

    public void shards(@Nullable UUID uuid, @Nullable UUID targetUuid, long shards) {
        builder()
                .query("""
                        INSERT INTO currency_transactions (
                            user_uuid,
                            target_uuid,
                            shards
                        ) VALUES (
                            ?,
                            ?,
                            ?
                        )""")
                .parameter(paramBuilder -> paramBuilder
                        .setUuidAsBytes(uuid)
                        .setUuidAsBytes(targetUuid)
                        .setLong(shards)
                )
                .update()
                .send();
    }

    public long shards(UUID uuid, int hours) {
        long send = builder(Long.class)
                .query("""
                            SELECT
                                SUM(shards) AS sumShards
                            FROM
                                currency_transactions
                            WHERE
                                user_uuid = ?
                              AND
                                create_date > DATE_SUB(CURRENT_TIMESTAMP, INTERVAL ? HOUR)
                            GROUP BY user_uuid""")
                .parameter(paramBuilder -> paramBuilder
                        .setUuidAsBytes(uuid)
                        .setInt(hours)
                )
                .readRow(row -> row.getLong("sumShards"))
                .firstSync()
                .orElse(0L);

        long received = builder(Long.class)
                .query("""
                            SELECT
                                SUM(shards) AS sumShards
                            FROM
                                currency_transactions
                            WHERE
                                target_uuid = ?
                              AND
                                create_date > DATE_SUB(CURRENT_TIMESTAMP, INTERVAL ? HOUR)
                            GROUP BY target_uuid""")
                .parameter(paramBuilder -> paramBuilder
                        .setUuidAsBytes(uuid)
                        .setInt(hours)
                )
                .readRow(row -> row.getLong("sumShards"))
                .firstSync()
                .orElse(0L);

        return received - send;
    }
}
