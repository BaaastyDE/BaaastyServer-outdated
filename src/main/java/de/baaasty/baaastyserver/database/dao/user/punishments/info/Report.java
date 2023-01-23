package de.baaasty.baaastyserver.database.dao.user.punishments.info;

import java.sql.Timestamp;
import java.util.UUID;

public record Report(int reportID, UUID user, UUID executed_user, UUID claimed_user, byte reasonID, Timestamp canceledDate, Timestamp createDate) {
}
