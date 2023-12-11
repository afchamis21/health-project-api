package andre.chamis.healthproject.service;

import andre.chamis.healthproject.domain.auth.model.RefreshTokenEntity;
import andre.chamis.healthproject.domain.auth.repository.RefreshTokenEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for managing refresh tokens.
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final JwtService jwtService;
    private final RefreshTokenEntityRepository refreshTokenEntityRepository;

    /**
     * Saves a refresh token to the database.
     *
     * @param refreshToken The refresh token to be saved.
     */
    public void saveTokenToDatabase(String refreshToken){
        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();

        refreshTokenEntity.setToken(refreshToken);
        refreshTokenEntity.setUsername(jwtService.getTokenSubject(refreshToken));
        refreshTokenEntity.setCreateDt(jwtService.getTokenIssuedAt(refreshToken));
        refreshTokenEntity.setExpireDt(jwtService.getTokenExpiresAt(refreshToken));

        refreshTokenEntityRepository.save(refreshTokenEntity);
    }

    /**
     * Checks if a refresh token exists in the database.
     *
     * @param token The refresh token to check.
     * @return True if the token exists, otherwise false.
     */
    public boolean existsOnDatabase(String token){
        return refreshTokenEntityRepository.existsByToken(token);
    }

    /**
     * Deletes all expired refresh tokens from the database.
     *
     * @return The number of deleted expired tokens.
     */
    public int deleteAllExpired(){
        return refreshTokenEntityRepository.deleteAllExpired();
    }

    /**
     * Deletes a specific refresh token from the database.
     *
     * @param refreshToken The refresh token to delete.
     */
    public void deleteToken(String refreshToken){
        refreshTokenEntityRepository.deleteRefreshToken(refreshToken);
    }

    /**
     * Deletes all refresh tokens associated with a specific username from the database.
     *
     * @param username The username for which refresh tokens should be deleted.
     */
    public void deleteTokenByUsername(String username) {
        refreshTokenEntityRepository.deleteByUsername(username);
    }
}
