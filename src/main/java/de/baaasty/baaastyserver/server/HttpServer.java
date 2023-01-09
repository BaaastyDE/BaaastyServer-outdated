package de.baaasty.baaastyserver.server;

import de.baaasty.baaastyserver.BaaastyServer;
import de.baaasty.baaastyserver.database.access.Users;
import io.javalin.Javalin;

import java.util.UUID;

public class HttpServer {
    private final Javalin javalin;
    private final Users users = BaaastyServer.instance().users();

    public HttpServer() {
        users.byUUID(UUID.fromString("7ccf6e1c-68fc-442d-88d0-341b315a29cd")).name("Baaasty");

        javalin = Javalin.create()
                .get("/", ctx -> ctx.json("Pong du Spast!"))
                .get("/auth/{token}", ctx -> ctx.json(AuthHandler.generateBearer(ctx.pathParam("token"))));

        addUserMetaRequests();

        javalin.start(8888);
    }

    private void addUserMetaRequests() {
        javalin.get("/user/uuid/{uuid}", ctx -> ctx.json(
                        users.cachedByUUID(UUID.fromString(ctx.pathParam("uuid")))
                ))
                .get("/user/uuid/{uuid}/name", ctx -> ctx.json(
                        users.cachedByUUID(UUID.fromString(ctx.pathParam("uuid")))
                                .name()
                ))
                .get("/user/uuid/{uuid}/name/{name}", ctx -> ctx.json(
                        users.cachedByUUID(UUID.fromString(ctx.pathParam("uuid")))
                                .name(ctx.pathParam("name"))
                ))
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
