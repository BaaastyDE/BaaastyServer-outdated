package de.baaasty.baaastyserver.server;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import de.baaasty.baaastyserver.BaaastyServer;
import de.baaasty.baaastyserver.database.access.Users;
import de.baaasty.baaastyserver.server.exception.InvalidAuthException;
import de.baaasty.baaastyserver.server.exception.MissingAuthException;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;

import java.util.Objects;
import java.util.UUID;

public class HttpServer {
    private final Javalin javalin;
    private final AuthHandler authHandler = new AuthHandler();
    private final Users users = BaaastyServer.instance().users();

    public HttpServer() {
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
                .get("/auth", ctx -> {
                    ctx.json(authHandler.generateBearer(ctx.body()));
                    ctx.status(200);
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
                });
    }

    public void stop() {
        this.javalin.stop();
    }
}
