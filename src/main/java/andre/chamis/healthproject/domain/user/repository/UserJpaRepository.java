package andre.chamis.healthproject.domain.user.repository;

import andre.chamis.healthproject.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing user entities.
 */
@Repository
interface UserJpaRepository extends JpaRepository<User, Long> {

    /**
     * Checks if a {@link User} with the given email exists.
     *
     * @param email The email to check for existence.
     * @return {@code true} if a user with the given email exists, otherwise {@code false}.
     */
    boolean existsByEmail(String email);

    /**
     * Finds a {@link User} by their username.
     *
     * @param username The username of the user to find.
     * @return An {@link Optional} containing the found user, or empty if not found.
     */
    Optional<User> findUserByUsername(String username);

    /**
     * Finds a {@link User} by their email.
     *
     * @param email The email of the user to find.
     * @return An {@link Optional} containing the found user, or empty if not found.
     */
    Optional<User> findUserByEmail(String email);

    /**
     * Retrieves a list of users with the specified registration completion status and whose update date is before the given expiration date.
     *
     * @param isRegistrationComplete The registration completion status to filter by.
     * @param expirationDt           The expiration date to compare the update date against.
     * @return A list of users matching the criteria.
     */
    @Query("SELECT u FROM User u WHERE u.isRegistrationComplete = ?1 AND u.updateDt < ?2 ")
    List<User> findAllByRegistrationCompleteAndUpdateDtBefore(boolean isRegistrationComplete, Date expirationDt);

    Optional<User> findByStripeClientId(String stripeClientId);
}
