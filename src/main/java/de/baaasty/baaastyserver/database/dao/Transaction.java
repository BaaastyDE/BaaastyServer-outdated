package de.baaasty.baaastyserver.database.dao;

import com.fasterxml.jackson.annotation.JsonGetter;

import java.util.UUID;

public record Transaction(
        @JsonGetter long id,
        @JsonGetter UUID userUuid,
        @JsonGetter UUID targetUuid,
        @JsonGetter long amethysts,
        @JsonGetter long shards
) {
}
