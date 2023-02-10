package de.baaasty.baaastyserver.database.dao.user;

import com.fasterxml.jackson.annotation.JsonGetter;
import de.baaasty.baaastyserver.database.dao.User;
import de.baaasty.baaastyserver.database.dao.user.currencies.Amethysts;
import de.baaasty.baaastyserver.database.dao.user.currencies.Shards;
import de.chojo.sadu.base.QueryFactory;

public class Currencies extends QueryFactory {
    private final Amethysts amethysts;
    private final Shards shards;

    public Currencies(User user) {
        super(user);
        this.amethysts = new Amethysts(user);
        this.shards = new Shards(user);
    }

    @JsonGetter
    public Amethysts amethysts() {
        return amethysts;
    }

    @JsonGetter
    public Shards shards() {
        return shards;
    }
}
