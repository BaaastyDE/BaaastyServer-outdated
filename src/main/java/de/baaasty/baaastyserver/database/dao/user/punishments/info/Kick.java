package de.baaasty.baaastyserver.database.dao.user.punishments.info;

import java.sql.Timestamp;
import java.util.UUID;

public record Kick(int kickID, UUID user, UUID executed_user, UUID canceled_user, byte reasonID, Timestamp canceledDate, Timestamp createDate) {
}
