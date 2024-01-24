package andre.chamis.healthproject.domain.auth.repository;

import andre.chamis.healthproject.domain.auth.model.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * Repository interface for managing refresh token entities using JPA.
 */
@Repository
interface RefreshTokenEntityJpaRepository extends JpaRepository<RefreshTokenEntity, Long> {
    /**
     * Deletes a refresh token by its token value.
     *
     * @param token The token value of the refresh token to delete.
     */
    void deleteByToken(String token);

    /**
     * Checks if a refresh token with the given token value exists.
     *
     * @param token The token value to check for existence.
     * @return {@code true} if a refresh token with the given token value exists, otherwise {@code false}.
     */
    boolean existsByToken(String token);

    /**
     * Deletes all refresh tokens that have an expiration date before the specified date.
     *
     * @param now The current date and time for comparison.
     * @return The number of deleted refresh tokens.
     */
    int deleteAllByExpireDtBefore(Date now);

    /**
     * Deletes all refresh tokens associated with a specific username.
     *
     * @param username The username of the user whose tokens are to be deleted.
     */
    void deleteAllByUsername(String username);
}
