package de.baaasty.baaastyserver.http.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import de.baaasty.baaastyserver.BaaastyServer;
import de.baaasty.baaastyserver.file.type.ConfigFile;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class AuthHandler implements Filter {
    private final Algorithm algorithm;
    private final String adminToken;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (httpRequest.getRequestURI().startsWith("/auth")) {
            chain.doFilter(request, response);
            return;
        }

        if (!isBearerValid(httpRequest.getHeader("Authorization"))) httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        chain.doFilter(request, response);
    }

    public void setErrorResponse(HttpStatus status, HttpServletResponse response, Throwable ex) {
        response.setStatus(status.value());
        response.setContentType("application/json");
        try {
            response.getWriter().write("Test");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public AuthHandler() {
        ConfigFile configFile = BaaastyServer.instance().configFile();
        algorithm = Algorithm.HMAC256(configFile.algorithmSecret());
        adminToken = configFile.adminToken();
    }

    public String generateBearer(String token) throws AuthenticationException {
        int accessLevel = checkToken(token);

        if (accessLevel == 99) {
            return JWT.create()
                    .withExpiresAt(Instant.now().plusSeconds(86400))
                    .sign(algorithm);
        } else throw new AuthenticationException("Not a valid token");
    }

    public boolean isBearerValid(String bearer) {
        try {
            JWT.require(algorithm)
                    .build()
                    .verify(bearer.substring(7)); // to cut away Bearer in front of the token

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
        if (token.equals(adminToken)) return 99;

        return 0;
    }
}
