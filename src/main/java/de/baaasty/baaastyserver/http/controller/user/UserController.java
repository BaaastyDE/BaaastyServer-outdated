package de.baaasty.baaastyserver.http.controller.user;

import de.baaasty.baaastyserver.BaaastyServer;
import de.baaasty.baaastyserver.database.access.Users;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}
