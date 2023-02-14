 package de.baaasty.baaastyserver.database.dao.user.currencies;

import com.fasterxml.jackson.annotation.JsonGetter;
import de.baaasty.baaastyserver.BaaastyServer;
import de.baaasty.baaastyserver.database.access.Transactions;
import de.baaasty.baaastyserver.database.dao.User;
import de.chojo.sadu.base.QueryFactory;

import java.util.UUID;

 public class Shards extends QueryFactory {
    private final User user;
    private final Transactions transactions = BaaastyServer.instance().transactions();
    private Long shards = null;

    public Shards(User user) {
        super(user);
        this.user = user;
    }

    @JsonGetter
    public Long get() {
        if (shards == null) {
            this.shards = builder(Long.class)
                    .query("""
                            SELECT
                                shards
                            FROM
                                user_currency
                            WHERE
                                user_uuid = ?""")
                    .parameter(paramBuilder -> paramBuilder.setUuidAsBytes(user.uuid()))
                    .readRow(row -> row.getLong("shards"))
                    .firstSync()
                    .orElse(0L);
        }

        return shards;
    }

    public void set(long shards) {
        long newShards = shards - get();

        if (newShards > 0) {
            transactions.shards(null, user.uuid(), newShards);
        } else {
            transactions.shards(user.uuid(), null, Math.abs(newShards));
        }

        builder()
                .query("""
                        INSERT INTO user_currency (
                            user_uuid,
                            shards
                        ) VALUES (
                            ?,
                            ?
                        )
                        ON DUPLICATE KEY
                        UPDATE
                        shards = ?""")
                .parameter(paramBuilder -> paramBuilder
                        .setUuidAsBytes(user.uuid())
                        .setLong(shards)
                        .setLong(shards)
                )
                .update()
                .send();

        this.shards = shards;
    }

    public void add(long shards) {
        add(shards, null);
    }

    public void add(long shards, UUID targetUuid) {
        long newShards = get() + shards;

        transactions.shards(targetUuid, user.uuid(), shards);

        builder()
                .query("""
                        INSERT INTO user_currency (
                            user_uuid,
                            shards
                        ) VALUES (
                            ?,
                            ?
                        )
                        ON DUPLICATE KEY
                        UPDATE
                        shards = ?""")
                .parameter(paramBuilder -> paramBuilder
                        .setUuidAsBytes(user.uuid())
                        .setLong(newShards)
                        .setLong(newShards)
                )
                .update()
                .send();

        this.shards = newShards;
    }

    public void remove(long shards) {
        remove(shards, null);
    }

    public void remove(long shards, UUID targetUuid) {
        long newShards = get() - shards;

        transactions.shards(user.uuid(), targetUuid, shards);

        builder()
                .query("""
                        INSERT INTO user_currency (
                            user_uuid,
                            shards
                        ) VALUES (
                            ?,
                            ?
                        )
                        ON DUPLICATE KEY
                        UPDATE
                        shards = ?""")
                .parameter(paramBuilder -> paramBuilder
                        .setUuidAsBytes(user.uuid())
                        .setLong(newShards)
                        .setLong(newShards)
                )
                .update()
                .send();

        this.shards = newShards;
    }
}
