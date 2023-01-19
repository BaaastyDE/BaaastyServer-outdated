package de.baaasty.baaastyserver.server;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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
                .get("/users/user/uuid/{cache}/{uuid}", ctx -> ctx.json(users.cachedByUUID(UUID.fromString(ctx.pathParam("uuid")), Boolean.parseBoolean(ctx.pathParam("cache")))))
                .get("/users/user/uuid/{cache}/{uuid}/uuid", ctx -> ctx.json(users.cachedByUUID(UUID.fromString(ctx.pathParam("uuid")), Boolean.parseBoolean(ctx.pathParam("cache"))).uuid().toString()))
                .get("/users/user/uuid/{cache}/{uuid}/name", ctx -> ctx.json(users.cachedByUUID(UUID.fromString(ctx.pathParam("uuid")), Boolean.parseBoolean(ctx.pathParam("cache"))).name()))
                .put("/users/user/uuid/{cache}/{uuid}/name/{name}", ctx -> {
                    users.cachedByUUID(UUID.fromString(ctx.pathParam("uuid")), Boolean.parseBoolean(ctx.pathParam("cache"))).name(ctx.pathParam("name"));
                    ctx.status(202);
                })
                .get("/users/user/uuid/{cache}/{uuid}/discordId", ctx -> ctx.json(users.cachedByUUID(UUID.fromString(ctx.pathParam("uuid")), Boolean.parseBoolean(ctx.pathParam("cache"))).discordId()))
                .put("/users/user/uuid/{cache}/{uuid}/discordId/{discordId}", ctx -> {
                    ctx.json(users.cachedByUUID(UUID.fromString(ctx.pathParam("uuid")), Boolean.parseBoolean(ctx.pathParam("cache"))).discordId(Long.parseLong(ctx.pathParam("discordId"))));
                    ctx.status(202);
                })
                .get("/users/user/uuid/{cache}/{uuid}/meta", ctx -> ctx.json(users.cachedByUUID(UUID.fromString(ctx.pathParam("uuid")), Boolean.parseBoolean(ctx.pathParam("cache"))).meta().cache()))
                .get("/users/user/uuid/{cache}/{uuid}/meta/language", ctx -> ctx.json(users.cachedByUUID(UUID.fromString(ctx.pathParam("uuid")), Boolean.parseBoolean(ctx.pathParam("cache"))).meta().language()))
                .put("/users/user/uuid/{cache}/{uuid}/meta/language/{language}", ctx -> {
                    ctx.json(users.cachedByUUID(UUID.fromString(ctx.pathParam("uuid")), Boolean.parseBoolean(ctx.pathParam("cache"))).meta().language(ctx.pathParam("language")));
                    ctx.status(202);
                })
                .get("/users/user/uuid/{cache}/{uuid}/meta/onlineTime", ctx -> ctx.json(users.cachedByUUID(UUID.fromString(ctx.pathParam("uuid")), Boolean.parseBoolean(ctx.pathParam("cache"))).meta().onlineTime()))
                .put("/users/user/uuid/{cache}/{uuid}/meta/onlineTime/{onlineTime}", ctx -> {
                    ctx.json(users.cachedByUUID(UUID.fromString(ctx.pathParam("uuid")), Boolean.parseBoolean(ctx.pathParam("cache"))).meta().onlineTime(Long.parseLong(ctx.pathParam("onlineTime"))));
                    ctx.status(202);
                })
                .get("/users/user/uuid/{cache}/{uuid}/meta/lastSeen", ctx -> ctx.json(users.cachedByUUID(UUID.fromString(ctx.pathParam("uuid")), Boolean.parseBoolean(ctx.pathParam("cache"))).meta().lastSeen()))
                .put("/users/user/uuid/{cache}/{uuid}/meta/updateLastSeen/", ctx -> {
                    ctx.json(users.cachedByUUID(UUID.fromString(ctx.pathParam("uuid")), Boolean.parseBoolean(ctx.pathParam("cache"))).meta().updateLastSeen());
                    ctx.status(202);
                })
                .get("/users/user/uuid/{cache}/{uuid}/meta/firstJoin", ctx -> ctx.json(users.cachedByUUID(UUID.fromString(ctx.pathParam("uuid")), Boolean.parseBoolean(ctx.pathParam("cache"))).meta().firstJoin()))
                .put("/users/user/uuid/{cache}/{uuid}/meta/setFirstJoin/", ctx -> {
                    ctx.json(users.cachedByUUID(UUID.fromString(ctx.pathParam("uuid")), Boolean.parseBoolean(ctx.pathParam("cache"))).meta().setFirstJoin());
                    ctx.status(202);
                });
    }

    public void stop() {
        this.javalin.stop();
    }
}
