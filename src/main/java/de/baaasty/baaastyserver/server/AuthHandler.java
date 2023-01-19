package de.baaasty.baaastyserver.server;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

import java.time.Instant;

public class AuthHandler {
    private static final Algorithm algorithm = Algorithm.HMAC256(System.getenv("ALGORITHM_SECRET"));

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
        if (System.getenv("SERVER_TOKEN_ADMIN").equals(token)) return 99;

        return 0;
    }
}
