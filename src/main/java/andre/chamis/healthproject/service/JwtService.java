package andre.chamis.healthproject.service;


import andre.chamis.healthproject.domain.auth.property.JwtProperties;
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
    private final JwtProperties jwtProperties;
    private final SessionService sessionService;
    private final String SESSION_PAYLOAD_KEY = "sessionId";

    private Algorithm accessTokenAlgorithm;
    private Algorithm refreshTokenAlgorithm;

    @PostConstruct
    void buildAlgorithms(){
        accessTokenAlgorithm = Algorithm.HMAC256(jwtProperties.getAccessToken().getEncryptionKey().getBytes());
        refreshTokenAlgorithm = Algorithm.HMAC256(jwtProperties.getRefreshToken().getEncryptionKey().getBytes());
    }

    /**
     * Creates an access token for a user session.
     *
     * @param username The username.
     * @param session The user session.
     * @return The generated access token.
     */
    public String createAccessToken(String username, Session session) {
        return JWT.create()
                .withSubject(username)
                .withIssuer(appName)
                .withClaim(SESSION_PAYLOAD_KEY, session.getSessionId())
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plus(
                        jwtProperties.getAccessToken().getDuration(),
                        jwtProperties.getAccessToken().getUnit()
                )).sign(accessTokenAlgorithm);
    }

    /**
     * Creates an access token for a user session.
     *
     * @param user The user.
     * @param session The user session.
     * @return The generated access token.
     */
    public String createAccessToken(User user, Session session){
        return createAccessToken(user.getUsername(), session);
    }

    /**
     * Creates a refresh token for a user session.
     *
     * @param username The username.
     * @param session The user session.
     * @return The generated refresh token.
     */
    public String createRefreshToken(String username, Session session){
        return JWT.create()
                .withSubject(username)
                .withIssuer(appName)
                .withClaim(SESSION_PAYLOAD_KEY, session.getSessionId())
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plus(
                        jwtProperties.getRefreshToken().getDuration(),
                        jwtProperties.getRefreshToken().getUnit()
                )).sign(refreshTokenAlgorithm);
    }

    /**
     * Creates a refresh token for a user session.
     *
     * @param user The user.
     * @param session The user session.
     * @return The generated refresh token.
     */
    public String createRefreshToken(User user, Session session){
        return createRefreshToken(user.getUsername(), session);
    }

    /**
     * Validates an access token.
     *
     * @param token The access token to validate.
     * @return True if the token is valid, otherwise false.
     */
    public boolean validateAccessToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(accessTokenAlgorithm).withIssuer(appName).build();
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
     * Validates a refresh token.
     *
     * @param token The refresh token to validate.
     * @return True if the token is valid, otherwise false.
     */
    public boolean validateRefreshToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(refreshTokenAlgorithm).withIssuer(appName).build();
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
