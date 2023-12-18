package andre.chamis.healthproject.service;

import andre.chamis.healthproject.domain.auth.dto.RefreshTokensDTO;
import andre.chamis.healthproject.domain.auth.dto.TokensDTO;
import andre.chamis.healthproject.domain.client.dto.ClientAuthDTO;
import andre.chamis.healthproject.domain.client.model.Client;
import andre.chamis.healthproject.domain.exception.UnauthorizedException;
import andre.chamis.healthproject.domain.response.ErrorMessage;
import andre.chamis.healthproject.domain.session.model.Session;
import andre.chamis.healthproject.domain.user.dto.GetUserDTO;
import andre.chamis.healthproject.domain.user.dto.LoginDTO;
import andre.chamis.healthproject.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

/**
 * Service class for user authentication, token management, and logout.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final SessionService sessionService;
    private final ClientService clientService;

    /**
     * Authenticates a user and generates access and refresh tokens.
     *
     * @param loginDTO The DTO containing user login credentials.
     * @return DTO containing access and refresh tokens.
     */
    public TokensDTO authenticateUser(LoginDTO loginDTO) {
        Optional<User> userOptional = userService.validateUserCredential(loginDTO);
        User user = userOptional.orElseThrow(() -> new UnauthorizedException(ErrorMessage.INVALID_CREDENTIALS));

        return generateSessionAndTokens(user);
    }

    /**
     * Refreshes access and refresh tokens.
     *
     * @param refreshTokensDTO The DTO containing the refresh token.
     * @return DTO containing new access and refresh tokens.
     */
    public TokensDTO refreshUserTokens(RefreshTokensDTO refreshTokensDTO) {
        String refreshToken = refreshTokensDTO.refreshToken();
        boolean isTokenValid = jwtService.validateUserRefreshToken(refreshToken);
        if (!isTokenValid) {
            refreshTokenService.deleteToken(refreshToken);
            throw new UnauthorizedException(ErrorMessage.INVALID_JWT);
        }

        boolean isTokenOnDatabase = refreshTokenService.existsOnDatabase(refreshToken);
        if (!isTokenOnDatabase) {
            throw new UnauthorizedException(ErrorMessage.INVALID_JWT);
        }
        String username = jwtService.getTokenSubject(refreshToken);

        Long sessionId = jwtService.getSessionIdFromToken(refreshToken);
        Optional<Session> sessionOptional = sessionService.findSessionById(sessionId);
        Session session = sessionOptional.orElseThrow(() -> new UnauthorizedException(ErrorMessage.EXPIRED_SESSION));

        boolean isSessionValid = sessionService.validateSessionIsNotExpired(session);
        if (!isSessionValid) {
            throw new UnauthorizedException(ErrorMessage.EXPIRED_SESSION);
        }

        String accessToken = jwtService.createUserAccessToken(username, session.getSessionId());

        Date refreshTokenExpirationDate = jwtService.getTokenExpiresAt(refreshToken);
        Duration durationUntilRefreshTokenExpires = Duration.between(
                Instant.now(),
                refreshTokenExpirationDate.toInstant()
        );

        if (durationUntilRefreshTokenExpires.toHours() <= 2) {
            refreshToken = jwtService.createUserRefreshToken(username, session.getSessionId());
        }

        return new TokensDTO(accessToken, refreshToken, (GetUserDTO) null);
    }

    /**
     * Logs out the current user by deleting refresh token and session.
     */
    public void logout() {
        User currentUser = userService.findCurrentUser();
        refreshTokenService.deleteTokenByUsername(currentUser.getUsername());
        sessionService.deleteCurrentSession();
    }

    /**
     * Generates a session and tokens for a user after successful authentication.
     *
     * @param user The authenticated user.
     * @return The generated access and refresh tokens along with user information.
     */
    private TokensDTO generateSessionAndTokens(User user) {
        // Create a session for the user.
        Session session = sessionService.createSession(user);

        // Generate access and refresh token for the user.
        String accessToken = jwtService.createUserAccessToken(user.getUsername(), session.getSessionId());
        String refreshToken = jwtService.createUserRefreshToken(user.getUsername(), session.getSessionId());

        // Saves refresh token on database.
        refreshTokenService.saveTokenToDatabase(refreshToken);

        return new TokensDTO(accessToken, refreshToken, user);
    }

    /**
     * Authenticates a client and generates access and refresh tokens.
     *
     * @param clientAuthDTO The DTO containing client authentication credentials.
     * @return The generated access and refresh tokens.
     * @throws UnauthorizedException If the client credentials are invalid.
     */
    public TokensDTO authenticateClient(ClientAuthDTO clientAuthDTO) {
        Optional<Client> clientOptional = clientService.validateClientCredentials(clientAuthDTO);
        Client client = clientOptional.orElseThrow(() -> new UnauthorizedException(ErrorMessage.INVALID_CREDENTIALS));

        return generateClientTokens(client);
    }

    /**
     * Generates access and refresh tokens for a client.
     *
     * @param client The client for which tokens are generated.
     * @return The generated access and refresh tokens.
     */
    private TokensDTO generateClientTokens(Client client) {
        String accessToken = jwtService.createClientAccessToken(client.getClientName());
        String refreshToken = jwtService.createClientRefreshToken(client.getClientName());

        refreshTokenService.saveTokenToDatabase(refreshToken);

        return new TokensDTO(accessToken, refreshToken, (GetUserDTO) null);
    }

    /**
     * Refreshes client tokens based on the provided refresh token.
     *
     * @param refreshTokensDTO The DTO containing the refresh token.
     * @return The refreshed access and refresh tokens.
     * @throws UnauthorizedException If the refresh token is invalid.
     */
    public TokensDTO refreshClientTokens(RefreshTokensDTO refreshTokensDTO) {
        String refreshToken = refreshTokensDTO.refreshToken();
        boolean isTokenValid = jwtService.validateClientRefreshToken(refreshToken);

        if (!isTokenValid) {
            refreshTokenService.deleteToken(refreshToken);
            throw new UnauthorizedException(ErrorMessage.INVALID_JWT);
        }

        boolean isTokenOnDatabase = refreshTokenService.existsOnDatabase(refreshToken);
        if (!isTokenOnDatabase) {
            throw new UnauthorizedException(ErrorMessage.INVALID_JWT);
        }

        String clientName = jwtService.getTokenSubject(refreshToken);

        String accessToken = jwtService.createClientAccessToken(clientName);

        Date refreshTokenExpirationDate = jwtService.getTokenExpiresAt(refreshToken);
        Duration durationUntilRefreshTokenExpires = Duration.between(
                Instant.now(),
                refreshTokenExpirationDate.toInstant()
        );

        if (durationUntilRefreshTokenExpires.toHours() <= 2) {
            refreshToken = jwtService.createClientRefreshToken(clientName);
        }

        return new TokensDTO(accessToken, refreshToken, (GetUserDTO) null);
    }
}
