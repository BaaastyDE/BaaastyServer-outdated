package de.baaasty.baaastyserver.database.dao.user;

import java.sql.Timestamp;

public record MetaInfo(String language, Long onlineTime, Timestamp lastSeen, Timestamp firstJoin) {
}
