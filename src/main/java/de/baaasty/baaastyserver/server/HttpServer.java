package de.baaasty.baaastyserver.server;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import de.baaasty.baaastyserver.BaaastyServer;
import de.baaasty.baaastyserver.database.access.Transactions;
import de.baaasty.baaastyserver.database.access.Users;
import de.baaasty.baaastyserver.database.dao.User;
import de.baaasty.baaastyserver.database.dao.user.punishments.info.*;
import de.baaasty.baaastyserver.server.exception.InvalidAuthException;
import de.baaasty.baaastyserver.server.exception.MissingAuthException;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class HttpServer {
    private final Javalin javalin;
    private final AuthHandler authHandler;
    private final Users users = BaaastyServer.instance().users();
    private final Transactions transactions = BaaastyServer.instance().transactions();

    public HttpServer(AuthHandler authHandler) {
        this.authHandler = authHandler;

        users.byUUID(UUID.fromString("9bbf53d5-b2a2-4d3f-b1a0-8e2d65fd2d94")).name("amonhtm");
        users.byUUID(UUID.fromString("7ccf6e1c-68fc-442d-88d0-341b315a29cd")).name("Baaasty");

        JsonMapper.builder().disable(
                MapperFeature.AUTO_DETECT_CREATORS,
                MapperFeature.AUTO_DETECT_FIELDS,
                MapperFeature.AUTO_DETECT_GETTERS,
                MapperFeature.AUTO_DETECT_IS_GETTERS,
                MapperFeature.AUTO_DETECT_SETTERS
        );

        javalin = Javalin.create(javalinConfig -> javalinConfig.jsonMapper(
                new JavalinJackson().updateMapper(
                        objectMapper -> objectMapper.disable(
                                MapperFeature.AUTO_DETECT_CREATORS,
                                MapperFeature.AUTO_DETECT_FIELDS,
                                MapperFeature.AUTO_DETECT_GETTERS,
                                MapperFeature.AUTO_DETECT_IS_GETTERS,
                                MapperFeature.AUTO_DETECT_SETTERS
                        )
                )
        ));

        registerAuthRequests();
        registerExceptions();
        registerUserRequests();

        javalin.start(8888);
    }

    private void registerAuthRequests() {
        javalin
                .get("/", ctx -> {
                    ctx.json("Pong!");
                    ctx.status(418);
                })
                .post("/auth", ctx -> {
                    String bearer = authHandler.generateBearer(ctx.body());
                    ctx.json(bearer);
                    ctx.status(200);

                    if (bearer.equals("Not a valid token")) ctx.status(401);
                });
    }

    private void registerExceptions() {
        javalin
                .before("/users/*", ctx -> {
                    if (ctx.header("Authorization") == null)
                        throw new MissingAuthException("Missing 'Authorization' header");

                    if (!authHandler.isBearerValid(Objects.requireNonNull(ctx.header("Authorization"))))
                        throw new InvalidAuthException("Please authorize at: '/auth'");
                })
                .exception(MissingAuthException.class, (exception, ctx) -> {
                    ctx.json(exception.getMessage());
                    ctx.status(401);
                })
                .exception(InvalidAuthException.class, (exception, ctx) -> {
                    ctx.json(exception.getMessage());
                    ctx.status(401);
                })
                .exception(NumberFormatException.class, (exception, ctx) -> {
                    ctx.json("Bad parameter! Failed to format! Maybe: parameter has to be a number");
                    ctx.status(415);
                });
    }

    private void registerUserRequests() {
        javalin
                /* IDEE

                .path("/users/user/name/{name}", ctx -> {
                    users.byName(ctx.pathParam("name"));
                })
                .path("/users/user/uuid/{uuid}", ctx -> {
                    users.byUUID(UUID.fromString(ctx.pathParam("uuid")));
                })
                */

                .get("/users/user/uuid/{uuid}", ctx -> ctx.json(users.byUUID(UUID.fromString(ctx.pathParam("uuid")))))
                .post("/users/user/uuid/{uuid}/cache", ctx -> users.addUserToCache(UUID.fromString(ctx.pathParam("uuid"))))
                .delete("/users/user/uuid/{uuid}/uncache", ctx -> users.removeUserFromCache(UUID.fromString(ctx.pathParam("uuid"))))
                .get("/users/user/uuid/{uuid}/uuid", ctx -> ctx.json(users.byUUID(UUID.fromString(ctx.pathParam("uuid"))).uuid().toString()))
                .get("/users/user/uuid/{uuid}/name", ctx -> ctx.json(users.byUUID(UUID.fromString(ctx.pathParam("uuid"))).name()))
                .patch("/users/user/uuid/{uuid}/name/{name}", ctx -> {
                    users.byUUID(UUID.fromString(ctx.pathParam("uuid"))).name(ctx.pathParam("name"));
                    ctx.status(202);
                })
                .get("/users/user/uuid/{uuid}/discordId", ctx -> ctx.json(users.byUUID(UUID.fromString(ctx.pathParam("uuid"))).discordId()))
                .patch("/users/user/uuid/{uuid}/discordId/{discordId}", ctx -> {
                    ctx.json(users.byUUID(UUID.fromString(ctx.pathParam("uuid"))).discordId(Long.parseLong(ctx.pathParam("discordId"))));
                    ctx.status(202);
                })
                .get("/users/user/uuid/{uuid}/meta", ctx -> ctx.json(users.byUUID(UUID.fromString(ctx.pathParam("uuid"))).meta()))
                .get("/users/user/uuid/{uuid}/meta/language", ctx -> ctx.json(users.byUUID(UUID.fromString(ctx.pathParam("uuid"))).meta().language()))
                .patch("/users/user/uuid/{uuid}/meta/language/{language}", ctx -> {
                    users.byUUID(UUID.fromString(ctx.pathParam("uuid"))).meta().language(ctx.pathParam("language"));
                    ctx.status(202);
                })
                .get("/users/user/uuid/{uuid}/meta/onlineTime", ctx -> ctx.json(users.byUUID(UUID.fromString(ctx.pathParam("uuid"))).meta().onlineTime()))
                .patch("/users/user/uuid/{uuid}/meta/onlineTime/{onlineTime}", ctx -> {
                    users.byUUID(UUID.fromString(ctx.pathParam("uuid"))).meta().onlineTime(Long.parseLong(ctx.pathParam("onlineTime")));
                    ctx.status(202);
                })
                .get("/users/user/uuid/{uuid}/meta/lastSeen", ctx -> ctx.json(users.byUUID(UUID.fromString(ctx.pathParam("uuid"))).meta().lastSeen()))
                .patch("/users/user/uuid/{uuid}/meta/updateLastSeen/", ctx -> {
                    users.byUUID(UUID.fromString(ctx.pathParam("uuid"))).meta().updateLastSeen();
                    ctx.status(202);
                })
                .get("/users/user/uuid/{uuid}/meta/firstJoin", ctx -> ctx.json(users.byUUID(UUID.fromString(ctx.pathParam("uuid"))).meta().firstJoin()))
                .patch("/users/user/uuid/{uuid}/meta/setFirstJoin/", ctx -> {
                    users.byUUID(UUID.fromString(ctx.pathParam("uuid"))).meta().setFirstJoin();
                    ctx.status(202);
                })
                .get("/users/user/uuid/{uuid}/currencies", ctx -> ctx.json(users.byUUID(UUID.fromString(ctx.pathParam("uuid"))).currencies()))
                .get("/users/user/uuid/{uuid}/currencies/amethysts", ctx -> ctx.json(users.byUUID(UUID.fromString(ctx.pathParam("uuid"))).currencies().amethysts().get()))
                .patch("/users/user/uuid/{uuid}/currencies/amethysts/set/{amethysts}", ctx -> {
                    users.byUUID(UUID.fromString(ctx.pathParam("uuid"))).currencies().amethysts().set(Long.parseLong(ctx.pathParam("amethysts")));
                    ctx.status(202);
                })
                .patch("/users/user/uuid/{uuid}/currencies/amethysts/add/{amethysts}", ctx -> {
                    users.byUUID(UUID.fromString(ctx.pathParam("uuid"))).currencies().amethysts().add(Long.parseLong(ctx.pathParam("amethysts")));
                    ctx.status(202);
                })
                .patch("/users/user/uuid/{uuid}/currencies/amethysts/add/{amethysts}/{targetUuid}", ctx -> {
                    users.byUUID(UUID.fromString(ctx.pathParam("uuid"))).currencies().amethysts().add(Long.parseLong(ctx.pathParam("amethysts")), UUID.fromString(ctx.pathParam("targetUuid")));
                    ctx.status(202);
                })
                .patch("/users/user/uuid/{uuid}/currencies/amethysts/remove/{amethysts}", ctx -> {
                    users.byUUID(UUID.fromString(ctx.pathParam("uuid"))).currencies().amethysts().remove(Long.parseLong(ctx.pathParam("amethysts")));
                    ctx.status(202);
                })
                .patch("/users/user/uuid/{uuid}/currencies/amethysts/remove/{amethysts}/{targetUuid}", ctx -> {
                    users.byUUID(UUID.fromString(ctx.pathParam("uuid"))).currencies().amethysts().remove(Long.parseLong(ctx.pathParam("amethysts")), UUID.fromString(ctx.pathParam("targetUuid")));
                    ctx.status(202);
                })
                .get("/users/user/uuid/{uuid}/currencies/shards", ctx -> ctx.json(users.byUUID(UUID.fromString(ctx.pathParam("uuid"))).currencies().shards().get()))
                .patch("/users/user/uuid/{uuid}/currencies/shards/set/{shards}", ctx -> {
                    users.byUUID(UUID.fromString(ctx.pathParam("uuid"))).currencies().shards().set(Long.parseLong(ctx.pathParam("shards")));
                    ctx.status(202);
                })
                .patch("/users/user/uuid/{uuid}/currencies/shards/add/{shards}", ctx -> {
                    users.byUUID(UUID.fromString(ctx.pathParam("uuid"))).currencies().shards().add(Long.parseLong(ctx.pathParam("shards")));
                    ctx.status(202);
                })
                .patch("/users/user/uuid/{uuid}/currencies/shards/add/{shards}/{targetUuid}", ctx -> {
                    users.byUUID(UUID.fromString(ctx.pathParam("uuid"))).currencies().shards().add(Long.parseLong(ctx.pathParam("shards")), UUID.fromString(ctx.pathParam("targetUuid")));
                    ctx.status(202);
                })
                .patch("/users/user/uuid/{uuid}/currencies/shards/remove/{shards}", ctx -> {
                    users.byUUID(UUID.fromString(ctx.pathParam("uuid"))).currencies().shards().remove(Long.parseLong(ctx.pathParam("shards")));
                    ctx.status(202);
                })
                .patch("/users/user/uuid/{uuid}/currencies/shards/remove/{shards}/{targetUuid}", ctx -> {
                    users.byUUID(UUID.fromString(ctx.pathParam("uuid"))).currencies().shards().remove(Long.parseLong(ctx.pathParam("shards")), UUID.fromString(ctx.pathParam("targetUuid")));
                    ctx.status(202);
                })
                .get("/users/user/uuid/{uuid}/punishments", ctx -> ctx.json(users.byUUID(UUID.fromString(ctx.pathParam("uuid"))).punishments()))
                .get("/users/user/uuid/{uuid}/punishments/bans", ctx -> ctx.json(users.byUUID(UUID.fromString(ctx.pathParam("uuid"))).punishments().bans().all()))
                .get("/users/user/uuid/{uuid}/punishments/bans/latest", ctx -> {
                    Optional<Ban> optBan = users.byUUID(UUID.fromString(ctx.pathParam("uuid"))).punishments().bans().latest();
                    ctx.json(optBan.isPresent() ? optBan.get() : "");
                })
                .post("/users/user/uuid/{uuid}/punishments/bans/add/{executedUuid}/{reasonId}/{duration}", ctx -> {
                    users.byUUID(UUID.fromString(ctx.pathParam("uuid"))).punishments().bans().add(
                            UUID.fromString(ctx.pathParam("executedUuid")),
                            Byte.parseByte(ctx.pathParam("reasonId")),
                            Long.parseLong(ctx.pathParam("duration"))
                    );
                    ctx.status(202);
                })
                .get("/users/user/uuid/{uuid}/punishments/kicks", ctx -> ctx.json(users.byUUID(UUID.fromString(ctx.pathParam("uuid"))).punishments().kicks().all()))
                .get("/users/user/uuid/{uuid}/punishments/kicks/latest", ctx -> {
                    Optional<Kick> optKick = users.byUUID(UUID.fromString(ctx.pathParam("uuid"))).punishments().kicks().latest();
                    ctx.json(optKick.isPresent() ? optKick.get() : "");
                })
                .post("/users/user/uuid/{uuid}/punishments/kicks/add/{executedUuid}/{reasonId}", ctx -> {
                    users.byUUID(UUID.fromString(ctx.pathParam("uuid"))).punishments().kicks().add(
                            UUID.fromString(ctx.pathParam("executedUuid")),
                            Byte.parseByte(ctx.pathParam("reasonId"))
                    );
                    ctx.status(202);
                })
                .get("/users/user/uuid/{uuid}/punishments/mutes", ctx -> ctx.json(users.byUUID(UUID.fromString(ctx.pathParam("uuid"))).punishments().mutes().all()))
                .get("/users/user/uuid/{uuid}/punishments/mutes/latest", ctx -> {
                    Optional<Mute> optMute = users.byUUID(UUID.fromString(ctx.pathParam("uuid"))).punishments().mutes().latest();
                    ctx.json(optMute.isPresent() ? optMute.get() : "");
                })
                .post("/users/user/uuid/{uuid}/punishments/mutes/{executedUuid}/{reasonId}/{duration}", ctx -> {
                    users.byUUID(UUID.fromString(ctx.pathParam("uuid"))).punishments().mutes().add(
                            UUID.fromString(ctx.pathParam("executedUuid")),
                            Byte.parseByte(ctx.pathParam("reasonId")),
                            Long.parseLong(ctx.pathParam("duration"))
                    );
                    ctx.status(202);
                })
                .get("/users/user/uuid/{uuid}/punishments/reports", ctx -> ctx.json(users.byUUID(UUID.fromString(ctx.pathParam("uuid"))).punishments().reports().all()))
                .get("/users/user/uuid/{uuid}/punishments/reports/latest", ctx -> {
                    Optional<Report> optReport = users.byUUID(UUID.fromString(ctx.pathParam("uuid"))).punishments().reports().latest();
                    ctx.json(optReport.isPresent() ? optReport.get() : "");
                })
                .post("/users/user/uuid/{uuid}/punishments/reports/{executedUuid}/{reasonId}", ctx -> {
                    users.byUUID(UUID.fromString(ctx.pathParam("uuid"))).punishments().reports().add(
                            UUID.fromString(ctx.pathParam("executedUuid")),
                            Byte.parseByte(ctx.pathParam("reasonId"))
                    );
                    ctx.status(202);
                })
                .get("/users/user/uuid/{uuid}/punishments/warns", ctx -> ctx.json(users.byUUID(UUID.fromString(ctx.pathParam("uuid"))).punishments().warns().all()))
                .get("/users/user/uuid/{uuid}/punishments/warns/latest", ctx -> {
                    Optional<Warn> optWarn = users.byUUID(UUID.fromString(ctx.pathParam("uuid"))).punishments().warns().latest();
                    ctx.json(optWarn.isPresent() ? optWarn.get() : "");
                })
                .post("/users/user/uuid/{uuid}/punishments/warns/{executedUuid}/{reasonId}", ctx -> {
                    users.byUUID(UUID.fromString(ctx.pathParam("uuid"))).punishments().warns().add(
                            UUID.fromString(ctx.pathParam("executedUuid")),
                            Byte.parseByte(ctx.pathParam("reasonId"))
                    );
                    ctx.status(202);
                })
                .get("/transactions/transaction/id/{id}", ctx -> ctx.json(transactions.byID(Long.parseLong(ctx.pathParam("id")))))
                .get("/transactions/transaction/uuid/{uuid}/amethysts/{hours}", ctx -> ctx.json(transactions.amethysts(UUID.fromString(ctx.pathParam("uuid")), Integer.parseInt(ctx.pathParam("hours")))))
                .get("/transactions/transaction/uuid/{uuid}/shards/{hours}", ctx -> ctx.json(transactions.shards(UUID.fromString(ctx.pathParam("uuid")), Integer.parseInt(ctx.pathParam("hours")))));
    }

    public void stop() {
        javalin.stop();
    }
}
