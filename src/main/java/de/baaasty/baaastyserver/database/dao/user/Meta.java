package de.baaasty.baaastyserver.database.dao.user;

import com.fasterxml.jackson.annotation.JsonGetter;
import de.baaasty.baaastyserver.database.dao.User;
import de.chojo.sadu.base.QueryFactory;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Meta extends QueryFactory {
    private final User user;
    private String language = null;
    private Long onlineTime = null;
    private Timestamp lastSeen = null;
    private Timestamp firstJoin = null;

    public Meta(User user) {
        super(user);
        this.user = user;
    }

    @JsonGetter
    public String language() {
        if (language == null) {
            this.language = builder(String.class)
                    .query("""
                            SELECT
                                language
                            FROM
                                user_meta
                            WHERE
                                user_uuid = ?""")
                    .parameter(paramBuilder -> paramBuilder.setUuidAsBytes(user.uuid()))
                    .readRow(row -> row.getString("language"))
                    .firstSync()
                    .orElse("DE");
        }

        return language;
    }

    public boolean language(String language) {
        boolean changed = builder()
                .query("""
                        INSERT INTO user_meta (
                            user_uuid,
                            language
                        ) VALUES (
                            ?,
                            ?
                        )
                        ON DUPLICATE KEY
                        UPDATE
                            language = ?""")
                .parameter(paramBuilder -> paramBuilder
                        .setUuidAsBytes(user.uuid())
                        .setString(language)
                        .setString(language)
                )
                .update()
                .sendSync()
                .changed();

        if (changed) this.language = language;

        return changed;
    }

    @JsonGetter
    public Long onlineTime() {
        if (onlineTime == null) {
            this.onlineTime = builder(Long.class)
                    .query("""
                            SELECT
                                online_time
                            FROM
                                user_meta
                            WHERE
                                user_uuid = ?""")
                    .parameter(paramBuilder -> paramBuilder.setUuidAsBytes(user.uuid()))
                    .readRow(row -> row.getLong("online_time"))
                    .firstSync()
                    .orElse(0L);
        }

        return onlineTime;
    }

    public boolean onlineTime(long onlineTime) {
        boolean changed = builder()
                .query("""
                        INSERT INTO user_meta (
                            user_uuid,
                            online_time
                        ) VALUES (
                            ?,
                            ?
                        )
                        ON DUPLICATE KEY
                        UPDATE
                        online_time = ?""")
                .parameter(paramBuilder -> paramBuilder
                        .setUuidAsBytes(user.uuid())
                        .setLong(onlineTime)
                        .setLong(onlineTime)
                )
                .update()
                .sendSync()
                .changed();

        if (changed) this.onlineTime = onlineTime;

        return changed;
    }

    @JsonGetter
    public Timestamp lastSeen() {
        if (lastSeen == null) {
            this.lastSeen = builder(Timestamp.class)
                    .query("""
                            SELECT
                                last_seen
                            FROM
                                user_meta
                            WHERE
                                user_uuid = ?""")
                    .parameter(paramBuilder -> paramBuilder.setUuidAsBytes(user.uuid()))
                    .readRow(row -> row.getTimestamp("last_seen"))
                    .firstSync()
                    .orElse(Timestamp.from(Instant.now()));
        }

        return lastSeen;
    }

    public boolean updateLastSeen() {
        Timestamp now = Timestamp.from(Instant.now());

        boolean changed = builder()
                .query("""
                        INSERT INTO user_meta (
                            user_uuid,
                            last_seen
                        ) VALUES (
                            ?,
                            ?
                        )
                        ON DUPLICATE KEY
                        UPDATE
                        last_seen = ?""")
                .parameter(paramBuilder -> paramBuilder
                        .setUuidAsBytes(user.uuid())
                        .setTimestamp(now)
                        .setTimestamp(now)
                )
                .update()
                .sendSync()
                .changed();

        if (changed) this.lastSeen = now;

        return changed;
    }

    @JsonGetter
    public Timestamp firstJoin() {
        if (firstJoin == null) {
            this.firstJoin = builder(Timestamp.class)
                    .query("""
                            SELECT
                                create_date
                            FROM
                                user_meta
                            WHERE
                                user_uuid = ?""")
                    .parameter(paramBuilder -> paramBuilder.setUuidAsBytes(user.uuid()))
                    .readRow(row -> row.getTimestamp("create_date"))
                    .firstSync()
                    .orElse(Timestamp.from(Instant.now()));
        }

        return firstJoin;
    }

    public boolean setFirstJoin() {
        Timestamp now = Timestamp.from(Instant.now());

        boolean changed = builder()
                .query("""
                        INSERT INTO user_meta (
                            user_uuid,
                            create_date
                        ) VALUES (
                            ?,
                            ?
                        )""")
                .parameter(paramBuilder -> paramBuilder
                        .setUuidAsBytes(user.uuid())
                        .setTimestamp(now)
                )
                .update()
                .sendSync()
                .changed();

        if (changed) this.firstJoin = now;

        return changed;
    }

    public Meta cache() {
        if (language != null && onlineTime != null && lastSeen != null && firstJoin != null) return this;

        List<String> selectValues = new ArrayList<>();

        if (language == null) selectValues.add("language");
        if (onlineTime == null) selectValues.add("onlineTime");
        if (lastSeen == null) selectValues.add("lastSeen");
        if (firstJoin == null) selectValues.add("firstJoin");

        MetaInfo metaInfo = builder(MetaInfo.class)
                .query("""
                        SELECT
                            ?
                        FROM
                            user_meta
                        WHERE
                            user_uuid = ?""")
                .parameter(paramBuilder -> paramBuilder
                        .setString(String.join(",", selectValues))
                        .setUuidAsBytes(user.uuid()))
                .readRow(row -> new MetaInfo(
                        language != null ? language : row.getString("language"),
                        onlineTime != null ? onlineTime : row.getLong("online_time"),
                        lastSeen != null ? lastSeen : row.getTimestamp("last_seen"),
                        firstJoin != null ? firstJoin : row.getTimestamp("create_date")
                ))
                .firstSync()
                .orElse(new MetaInfo("DE", 0L, Timestamp.from(Instant.now()), Timestamp.from(Instant.now())));

        this.language = metaInfo.language();
        this.onlineTime = metaInfo.onlineTime();
        this.lastSeen = metaInfo.lastSeen();
        this.firstJoin = metaInfo.firstJoin();

        return this;
    }
}
