package de.baaasty.baaastyserver.database.dao.user;

import com.fasterxml.jackson.annotation.JsonGetter;
import de.baaasty.baaastyserver.database.dao.User;
import de.chojo.sadu.base.QueryFactory;

public class Currencies extends QueryFactory {
    private final User user;
    private Long amethysts = null;
    private Long shards = null;

    public Currencies(User user) {
        super(user);
        this.user = user;
    }

    @JsonGetter
    public Long amethysts() {
        if (amethysts == null) {
            this.amethysts = builder(Long.class)
                    .query("""
                            SELECT
                                amethysts
                            FROM
                                user_currency
                            WHERE
                                user_uuid = ?""")
                    .parameter(paramBuilder -> paramBuilder.setUuidAsBytes(user.uuid()))
                    .readRow(row -> row.getLong("amethysts"))
                    .firstSync()
                    .orElse(0L);
        }

        return amethysts;
    }

    public void amethysts(long amethysts) {
        builder()
                .query("""
                        INSERT INTO user_currency (
                            user_uuid,
                            amethysts
                        ) VALUES (
                            ?,
                            ?
                        )
                        ON DUPLICATE KEY
                        UPDATE
                        amethysts = ?""")
                .parameter(paramBuilder -> paramBuilder
                        .setUuidAsBytes(user.uuid())
                        .setLong(amethysts)
                        .setLong(amethysts)
                )
                .update()
                .send();

        this.amethysts = amethysts;
    }

    @JsonGetter
    public Long shards() {
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

    public void shards(long shards) {
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
}
