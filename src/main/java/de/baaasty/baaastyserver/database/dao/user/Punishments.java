package de.baaasty.baaastyserver.database.dao.user;

import com.fasterxml.jackson.annotation.JsonGetter;
import de.baaasty.baaastyserver.database.dao.User;
import de.baaasty.baaastyserver.database.dao.user.punishments.*;

public class Punishments {
    private final Reports reports;
    private final Warns warns;
    private final Mutes mutes;
    private final Kicks kicks;
    private final Bans bans;

    public Punishments(User user) {
        this.reports = new Reports(user);
        this.warns = new Warns(user);
        this.mutes = new Mutes(user);
        this.kicks = new Kicks(user);
        this.bans = new Bans(user);
    }

    @JsonGetter
    public Reports reports() {
        return reports;
    }

    @JsonGetter
    public Warns warns() {
        return warns;
    }

    @JsonGetter
    public Mutes mutes() {
        return mutes;
    }

    @JsonGetter
    public Kicks kicks() {
        return kicks;
    }

    @JsonGetter
    public Bans bans() {
        return bans;
    }
}
