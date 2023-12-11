package andre.chamis.healthproject.domain.auth.repository;

import andre.chamis.healthproject.domain.auth.model.RefreshTokenEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Date;

/**
 * Repository class for managing refresh token entities using JPA.
 */
@Repository
@RequiredArgsConstructor
public class RefreshTokenEntityRepository {
    private final RefreshTokenEntityJpaRepository jpaRepository;

    /**
     * Saves a refresh token entity.
     *
     * @param refreshTokenEntity The refresh token entity to be saved.
     * @return The saved refresh token entity.
     */
    public RefreshTokenEntity save(RefreshTokenEntity refreshTokenEntity) {
        return jpaRepository.save(refreshTokenEntity);
    }

    /**
     * Deletes a refresh token by its token value.
     *
     * @param refreshToken The token value of the refresh token to delete.
     */
    @Transactional
    public void deleteRefreshToken(String refreshToken){
        jpaRepository.deleteByToken(refreshToken);
    }

    /**
     * Checks if a refresh token with the given token value exists.
     *
     * @param token The token value to check for existence.
     * @return {@code true} if a refresh token with the given token value exists, otherwise {@code false}.
     */
    public boolean existsByToken(String token) {
        return jpaRepository.existsByToken(token);
    }

    /**
     * Deletes all expired refresh tokens.
     *
     * @return The number of deleted refresh tokens.
     */
    @Transactional
    public int deleteAllExpired() {
        return jpaRepository.deleteAllByExpireDtBefore(Date.from(Instant.now()));
    }

    /**
     * Deletes all refresh tokens associated with a specific username.
     *
     * @param username The username of the user whose tokens are to be deleted.
     */
    @Transactional
    public void deleteByUsername(String username){
        jpaRepository.deleteAllByUsername(username);
    }
}
