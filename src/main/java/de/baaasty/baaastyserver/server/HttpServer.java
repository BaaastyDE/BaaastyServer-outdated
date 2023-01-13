package de.baaasty.baaastyserver.server;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import de.baaasty.baaastyserver.BaaastyServer;
import de.baaasty.baaastyserver.database.access.Users;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;

import java.util.UUID;

public class HttpServer {
    private final Javalin javalin;
    private final Users users = BaaastyServer.instance().users();

    public HttpServer() {
        users.byUUID(UUID.fromString("7ccf6e1c-68fc-442d-88d0-341b315a29cd")).name("Baaasty");

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
                ))
                .get("/", ctx -> ctx.json("Pong!"))
                .get("/auth", ctx -> {
                    ctx.result(AuthHandler.generateBearer(ctx.body()));
                    ctx.status(200);
                });

        addUserMetaRequests();

        javalin.start(8888);
    }

    private void addUserMetaRequests() {
        javalin
                .get("/user/uuid/{uuid}", ctx -> ctx.json(
                        users.cachedByUUID(UUID.fromString(ctx.pathParam("uuid")))
                ))
                .get("/user/uuid/{uuid}/uuid", ctx -> ctx.json(
                        users.cachedByUUID(UUID.fromString(ctx.pathParam("uuid")))
                                .uuid()
                ))
                .get("/user/uuid/{uuid}/name", ctx -> ctx.json(
                        users.cachedByUUID(UUID.fromString(ctx.pathParam("uuid")))
                                .name()
                ))
                .get("/user/uuid/{uuid}/name/{name}", ctx -> ctx.json(
                        users.cachedByUUID(UUID.fromString(ctx.pathParam("uuid")))
                                .name(ctx.pathParam("name"))
                ))
                .get("/user/uuid/{uuid}/discordId", ctx -> ctx.json(
                        users.cachedByUUID(UUID.fromString(ctx.pathParam("uuid")))
                                .discordId()
                ))
                .get("/user/uuid/{uuid}/discordId/{discordId}", ctx -> {
                    users.cachedByUUID(UUID.fromString(ctx.pathParam("uuid")))
                            .discordId(Long.parseLong(ctx.pathParam("discordId")));
                    ctx.json(
                            "lol"
                    );
                })
                .get("/user/uuid/{uuid}/meta/language", ctx -> ctx.json(
                        users.cachedByUUID(UUID.fromString(ctx.pathParam("uuid")))
                                .meta()
                                .language()
                ))
                .get("/user/uuid/{uuid}/meta/language/{language}", ctx -> ctx.json(
                        users.cachedByUUID(UUID.fromString(ctx.pathParam("uuid")))
                                .meta()
                                .language(ctx.pathParam("language"))
                ));
    }

    public void stop() {
        this.javalin.stop();
    }
}
