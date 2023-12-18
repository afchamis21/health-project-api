package andre.chamis.healthproject.service;


import andre.chamis.healthproject.domain.auth.property.AuthProperties;
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
     * Creates an access token for a user based on the provided username and session ID.
     *
     * @param username  The username for which the token is created.
     * @param sessionId The session ID associated with the user.
     * @return The generated user access token.
     */
    public String createUserAccessToken(String username, Long sessionId) {
        return JWT.create()
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
     * Creates a refresh token for a user based on the provided username and session ID.
     *
     * @param username  The username for which the token is created.
     * @param sessionId The session ID associated with the user.
     * @return The generated user refresh token.
     */
    public String createUserRefreshToken(String username, Long sessionId) {
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
     * Creates an access token for a client based on the provided client name.
     *
     * @param name The client name for which the token is created.
     * @return The generated client access token.
     */
    public String createClientAccessToken(String name) {
        return JWT.create()
                .withSubject(name)
                .withIssuer(appName)
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plus(
                        authProperties.getUser().getAccessToken().getDuration(),
                        authProperties.getUser().getAccessToken().getUnit()
                )).sign(clientAccessTokenAlgorithm);
    }

    /**
     * Creates a refresh token for a client based on the provided client name.
     *
     * @param name The client name for which the token is created.
     * @return The generated client refresh token.
     */
    public String createClientRefreshToken(String name) {
        return JWT.create()
                .withSubject(name)
                .withIssuer(appName)
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plus(
                        authProperties.getUser().getRefreshToken().getDuration(),
                        authProperties.getUser().getRefreshToken().getUnit()
                )).sign(clientRefreshTokenAlgorithm);
    }

    /**
     * Validates a user access token.
     *
     * @param token The access token to validate.
     * @return True if the token is valid, otherwise false.
     */
    public boolean validateUserAccessToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(userAccessTokenAlgorithm).withIssuer(appName).build();
            verifier.verify(token);
            return true;
        } catch (TokenExpiredException ex) {
            Long sessionId = getSessionIdFromToken(token);
            sessionService.deleteSessionById(sessionId);
            return false;
        } catch (JWTVerificationException ex) {
            return false;
        }
    }

    /**
     * Validates a client access token.
     *
     * @param token The access token to validate.
     * @return True if the token is valid, otherwise false.
     */
    public boolean validateClientAccessToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(clientAccessTokenAlgorithm).withIssuer(appName).build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException ex) {
            return false;
        }
    }

    /**
     * Validates a user refresh token.
     *
     * @param token The refresh token to validate.
     * @return True if the token is valid, otherwise false.
     */
    public boolean validateUserRefreshToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(userRefreshTokenAlgorithm).withIssuer(appName).build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException ex) {
            return false;
        }
    }

    /**
     * Validates a client refresh token.
     *
     * @param token The refresh token to validate.
     * @return True if the token is valid, otherwise false.
     */
    public boolean validateClientRefreshToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(clientRefreshTokenAlgorithm).withIssuer(appName).build();
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
}
