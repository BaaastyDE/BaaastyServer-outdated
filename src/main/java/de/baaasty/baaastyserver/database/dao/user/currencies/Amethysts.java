package de.baaasty.baaastyserver.database.dao.user.currencies;

import com.fasterxml.jackson.annotation.JsonGetter;
import de.baaasty.baaastyserver.BaaastyServer;
import de.baaasty.baaastyserver.database.access.Transactions;
import de.baaasty.baaastyserver.database.dao.User;
import de.chojo.sadu.base.QueryFactory;

import java.util.UUID;

public class Amethysts extends QueryFactory {
    private final User user;
    private final Transactions transactions = BaaastyServer.instance().transactions();
    private Long amethysts = null;

    public Amethysts(User user) {
        super(user);
        this.user = user;
    }

    @JsonGetter
    public Long get() {
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

    public void set(long amethysts) {
        long newAmethysts = amethysts - get();

        if (newAmethysts > 0) {
            transactions.amethysts(null, user.uuid(), newAmethysts);
        } else {
            transactions.amethysts(user.uuid(), null, Math.abs(newAmethysts));
        }

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

    public void add(long amethysts) {
        add(amethysts, null);
    }

    public void add(long amethysts, UUID targetUuid) {
        long newAmethysts = get() + amethysts;

        transactions.amethysts(targetUuid, user.uuid(), amethysts);

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
                        .setLong(newAmethysts)
                        .setLong(newAmethysts)
                )
                .update()
                .send();

        this.amethysts = newAmethysts;
    }

    public void remove(long amethysts) {
        remove(amethysts, null);
    }

    public void remove(long amethysts, UUID targetUuid) {
        long newAmethysts = get() - amethysts;

        transactions.amethysts(user.uuid(), targetUuid, amethysts);

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
                        .setLong(newAmethysts)
                        .setLong(newAmethysts)
                )
                .update()
                .send();

        this.amethysts = newAmethysts;
    }
}
