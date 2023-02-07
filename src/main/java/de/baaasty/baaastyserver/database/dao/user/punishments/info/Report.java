package de.baaasty.baaastyserver.database.dao.user.punishments.info;

import com.fasterxml.jackson.annotation.JsonGetter;

import javax.annotation.Nullable;
import java.sql.Timestamp;
import java.util.UUID;

public record Report(
        @JsonGetter int reportID,
        @JsonGetter UUID user,
        @JsonGetter UUID executed_user,
        @JsonGetter @Nullable UUID claimed_user,
        @JsonGetter byte reasonID,
        @JsonGetter @Nullable Timestamp canceledDate,
        @JsonGetter Timestamp createDate
) {
}
