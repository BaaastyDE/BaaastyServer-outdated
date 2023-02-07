package de.baaasty.baaastyserver.database.dao.user.punishments.info;

import com.fasterxml.jackson.annotation.JsonGetter;

import javax.annotation.Nullable;
import java.sql.Timestamp;
import java.util.UUID;

public record Warn(
        @JsonGetter int warnID,
        @JsonGetter UUID user,
        @JsonGetter UUID executed_user,
        @JsonGetter @Nullable UUID canceled_user,
        @JsonGetter byte reasonID,
        @JsonGetter @Nullable Timestamp canceledDate,
        @JsonGetter Timestamp createDate
) {
}
