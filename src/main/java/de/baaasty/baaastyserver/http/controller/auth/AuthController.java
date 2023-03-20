package de.baaasty.baaastyserver.http.controller.auth;

import de.baaasty.baaastyserver.BaaastyServer;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class AuthController {
    @RequestMapping("/")
    @ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
    public String ping() {
        return "Pong!";
    }

    @PostMapping(value = "/auth")
    public BearerDTO auth(@RequestBody TokenDTO tokenDTO) {
        try {
            return new BearerDTO(BaaastyServer.instance().authHandler().generateBearer(tokenDTO.token()));
        } catch (AuthenticationException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, exception.getMessage(), exception);
        }
    }
}
