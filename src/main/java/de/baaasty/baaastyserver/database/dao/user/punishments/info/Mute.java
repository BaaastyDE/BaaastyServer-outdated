package de.baaasty.baaastyserver.database.dao.user.punishments.info;

import java.sql.Timestamp;
import java.util.UUID;

public record Mute(int muteID, UUID user, UUID executed_user, UUID canceled_user, byte reasonID, Timestamp untilDate, Timestamp canceledDate, Timestamp createDate) {
}
