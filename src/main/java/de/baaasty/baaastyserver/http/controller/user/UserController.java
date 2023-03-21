package de.baaasty.baaastyserver.http.controller.user;

import de.baaasty.baaastyserver.BaaastyServer;
import de.baaasty.baaastyserver.database.access.Users;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class UserController {
    private final Users users = BaaastyServer.instance().users();

    @PostMapping(value = "/userCache")
    public void addUserCache(@RequestBody UuidDTO uuidDTO) {
        users.addUserToCache(uuidDTO.uuid());
    }

    @DeleteMapping(value = "/userCache")
    public void removeUserCache(@RequestBody UuidDTO uuidDTO) {
        users.removeUserFromCache(uuidDTO.uuid());
    }

    @GetMapping(value = "/user")
    public void user(@RequestParam(required = false) UUID uuid, @RequestParam(required = false) String name) {

    }
}
