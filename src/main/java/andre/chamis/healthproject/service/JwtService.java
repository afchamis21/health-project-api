package andre.chamis.healthproject.service;


import andre.chamis.healthproject.domain.auth.property.AuthProperties;
import andre.chamis.healthproject.domain.client.model.Client;
import andre.chamis.healthproject.domain.session.model.Session;
import andre.chamis.healthproject.domain.user.model.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

/**
 * Service class for managing JSON Web Tokens (JWT).
 */
@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${spring.application.name}")
    private String appName;
    private final AuthProperties authProperties;
    private final SessionService sessionService;
    private final String SESSION_PAYLOAD_KEY = "sessionId";

    private Algorithm userAccessTokenAlgorithm;
    private Algorithm userRefreshTokenAlgorithm;
    private Algorithm clientAccessTokenAlgorithm;
    private Algorithm clientRefreshTokenAlgorithm;

    @PostConstruct
    void buildAlgorithms() {
        userAccessTokenAlgorithm = Algorithm.HMAC256(authProperties.getUser().getAccessToken().getEncryptionKey().getBytes());
        userRefreshTokenAlgorithm = Algorithm.HMAC256(authProperties.getUser().getRefreshToken().getEncryptionKey().getBytes());

        clientAccessTokenAlgorithm = Algorithm.HMAC256(authProperties.getClient().getAccessToken().getEncryptionKey().getBytes());
        clientRefreshTokenAlgorithm = Algorithm.HMAC256(authProperties.getClient().getRefreshToken().getEncryptionKey().getBytes());
    }

    /**
     * Creates an access token for a user session.
     *
     * @param username  The username.
     * @param sessionId The id of the user session.
     * @return The generated access token.
     */
    public String createAccessToken(String username, Long sessionId) {
        return JWT.create() // TODO check all verifications and creations of jwts to see if a null subject can cause vulnerabilites or bugs
                .withSubject(username)
                .withIssuer(appName)
                .withClaim(SESSION_PAYLOAD_KEY, sessionId)
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plus(
                        authProperties.getUser().getAccessToken().getDuration(),
                        authProperties.getUser().getAccessToken().getUnit()
                )).sign(userAccessTokenAlgorithm);
    }

    /**
     * Creates an access token for a user session.
     *
     * @param user    The user.
     * @param session The user session.
     * @return The generated access token.
     */
    public String createAccessToken(User user, Session session) {
        return createAccessToken(user.getUsername(), session.getSessionId());
    }

    /**
     * Creates a refresh token for a user session.
     *
     * @param username  The username.
     * @param sessionId The id of the user session.
     * @return The generated refresh token.
     */
    public String createRefreshToken(String username, Long sessionId) {
        return JWT.create()
                .withSubject(username)
                .withIssuer(appName)
                .withClaim(SESSION_PAYLOAD_KEY, sessionId)
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plus(
                        authProperties.getUser().getRefreshToken().getDuration(),
                        authProperties.getUser().getRefreshToken().getUnit()
                )).sign(userRefreshTokenAlgorithm);
    }

    /**
     * Creates a refresh token for a user session.
     *
     * @param user    The user.
     * @param session The user session.
     * @return The generated refresh token.
     */
    public String createRefreshToken(User user, Session session) {
        return createRefreshToken(user.getUsername(), session.getSessionId());
    }

    /**
     * Validates an access token.
     *
     * @param token The access token to validate.
     * @return True if the token is valid, otherwise false.
     */
    public boolean validateAccessToken(String token) { // TODO REFACTOR TO SUPPORT CLIENT
        try {
            JWTVerifier verifier = JWT.require(userAccessTokenAlgorithm).withIssuer(appName).build();
            verifier.verify(token);
            return true;
        } catch (TokenExpiredException ex) {
            Long sessionId = getSessionIdFromToken(token);
            sessionService.deleteSessionById(sessionId); // TODO move to interceptor
            return false;
        } catch (JWTVerificationException ex) {
            return false;
        }
    }

    /**
     * Validates a refresh token.
     *
     * @param token The refresh token to validate.
     * @return True if the token is valid, otherwise false.
     */
    public boolean validateRefreshToken(String token) { // TODO REFACTOR TO SUPPORT CLIENT
        try {
            JWTVerifier verifier = JWT.require(userRefreshTokenAlgorithm).withIssuer(appName).build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException ex) {
            return false;
        }
    }

    /**
     * Validates a refresh token.
     *
     * @param token The refresh token to validate.
     * @return True if the token is valid, otherwise false.
     */
    public Date getTokenExpiresAt(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getExpiresAt();
    }

    /**
     * Retrieves the issuance time of a token.
     *
     * @param token The token to examine.
     * @return The issuance time as a Date.
     */
    public Date getTokenIssuedAt(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getIssuedAt();
    }

    /**
     * Retrieves the subject (username) of a token.
     *
     * @param token The token to examine.
     * @return The subject (username) of the token.
     */
    public String getTokenSubject(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getSubject();
    }

    /**
     * Retrieves the session ID from a token.
     *
     * @param token The token to examine.
     * @return The session ID contained in the token.
     */
    public Long getSessionIdFromToken(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getClaim(SESSION_PAYLOAD_KEY).asLong();
    }

    public String createAccessToken(Client client) {
        return createAccessToken(client.getClientName(), null);
    }

    public String createRefreshToken(Client client) {
        return createAccessToken(client.getClientName(), null);
    }
}
