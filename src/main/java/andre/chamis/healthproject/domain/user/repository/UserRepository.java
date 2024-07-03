package andre.chamis.healthproject.domain.user.repository;

import andre.chamis.healthproject.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Repository class for managing user entities using both JPA and in-memory caching.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final UserJpaRepository userJpaRepository;
    private final UserInMemoryCache userInMemoryCache;

    /**
     * Finds a user by their ID, first checking the in-memory cache, then the database.
     *
     * @param userId The ID of the user to find.
     * @return An {@link Optional} containing the found user, or empty if not found.
     */
    public Optional<User> findById(Long userId) {
        Optional<User> userOptionalFromCache = userInMemoryCache.get(userId);
        if (userOptionalFromCache.isPresent()) {
            return userOptionalFromCache;
        }

        Optional<User> userOptionalFromDatabase = userJpaRepository.findById(userId);
        userOptionalFromDatabase.ifPresent(userInMemoryCache::put);

        return userOptionalFromDatabase;
    }

    /**
     * Saves a user, updating both the database and the in-memory cache.
     * The update date of the user is set to the current date and time.
     *
     * @param user The user to be saved.
     * @return The saved user.
     */
    public User save(User user) {
        user.setUpdateDt(Date.from(Instant.now()));

        user = userJpaRepository.save(user);

        userInMemoryCache.put(user);

        return user;
    }

    /**
     * Checks if a user with the given email exists.
     *
     * @param email The email to check for existence.
     * @return {@code true} if a user with the given email exists, otherwise {@code false}.
     */
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    /**
     * Deletes a user by their ID from both the database and the in-memory cache.
     *
     * @param userId The ID of the user to delete.
     */
    public void delete(Long userId) {
        userInMemoryCache.remove(userId);
        userJpaRepository.deleteById(userId);
    }

    public Optional<User> findUserByEmail(String email) {
        return userJpaRepository.findUserByEmail(email);
    }

    /**
     * Retrieves a list of users with expired and incomplete registrations based on the provided expiration date.
     *
     * @param expirationDt The expiration date to determine registration expiration.
     * @return A list of users with expired and incomplete registrations.
     */
    public List<User> findAllWithExpiredIncompleteRegistrations(Date expirationDt) {
        return userJpaRepository.findAllByRegistrationCompleteAndUpdateDtBefore(false, expirationDt);
    }

    /**
     * Finds a user by their Stripe client ID.
     *
     * @param stripeClientId The Stripe client ID of the user to find.
     * @return An {@link Optional} containing the found user, or empty if not found.
     */
    public Optional<User> findUserByStripeClientId(String stripeClientId) {
        return userJpaRepository.findByStripeClientId(stripeClientId);
    }
}
