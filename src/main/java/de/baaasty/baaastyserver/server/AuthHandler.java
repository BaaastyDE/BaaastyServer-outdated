package de.baaasty.baaastyserver.server;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import de.baaasty.baaastyserver.BaaastyServer;
import de.baaasty.baaastyserver.file.type.ConfigFile;

import java.time.Instant;

public class AuthHandler {
    private final Algorithm algorithm;
    private final String adminToken;

    public AuthHandler(ConfigFile configFile) {
        algorithm = Algorithm.HMAC256(configFile.algorithmSecret());
        adminToken = configFile.adminToken();
    }

    public String generateBearer(String token) {
        int accessLevel = checkToken(token);

        if (accessLevel == 99) {
            return JWT.create()
                    .withExpiresAt(Instant.now().plusSeconds(86400))
                    .sign(algorithm);
        } else return "Not a valid token";
    }

    public boolean isBearerValid(String token) {
        try {
            JWT.require(algorithm)
                    .build()
                    .verify(token.substring(7)); // to cut away Bearer in front of the token

            return true;
        } catch (JWTVerificationException exception) {
            return false;
        }
    }

    /**
     * Checks for access level with a token
     *
     * @param token Gets checked with env vars
     * @return The access level
     */
    public int checkToken(String token) {
        System.out.println("\"" + token + "\"");
        System.out.println("\"" + adminToken + "\"");

        if (token.equals(adminToken)) return 99;

        return 0;
    }
}
