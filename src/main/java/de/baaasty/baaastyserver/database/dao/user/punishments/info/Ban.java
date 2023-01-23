package de.baaasty.baaastyserver.database.dao.user.punishments.info;

import com.fasterxml.jackson.annotation.JsonGetter;

import javax.annotation.Nullable;
import java.sql.Timestamp;
import java.util.UUID;

public record Ban(
        @JsonGetter int banID,
        @JsonGetter UUID user,
        @JsonGetter UUID executed_user,
        @JsonGetter @Nullable UUID canceled_user,
        @JsonGetter byte reasonID,
        @JsonGetter Timestamp untilDate,
        @JsonGetter @Nullable Timestamp canceledDate,
        @JsonGetter Timestamp createDate) {
}
