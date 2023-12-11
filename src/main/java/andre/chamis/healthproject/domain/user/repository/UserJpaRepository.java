package andre.chamis.healthproject.domain.user.repository;

import andre.chamis.healthproject.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing user entities.
 */
@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> {
    /**
     * Checks if a {@link User} with the given username exists.
     *
     * @param username The username to check for existence.
     * @return {@code true} if a user with the given username exists, otherwise {@code false}.
     */
    boolean existsByUsername(String username);

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
     * @param email The username of the user to find.
     * @return An {@link Optional} containing the found user, or empty if not found.
     */
    Optional<User> findUserByEmail(String email);
}
