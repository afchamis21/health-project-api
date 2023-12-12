package andre.chamis.healthproject.domain.auth.repository;

import andre.chamis.healthproject.domain.auth.model.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing OTP (One-Time Password) entities.
 */
@Repository
public interface OtpJpaRepository extends JpaRepository<OTP, Long> {

    /**
     * Checks if an OTP exists for the specified user ID.
     *
     * @param userId The ID of the user to check for an existing OTP.
     * @return {@code true} if an OTP exists for the user, otherwise {@code false}.
     */
    boolean existsByUserId(Long userId);

    /**
     * Checks if an OTP with the specified code exists.
     *
     * @param code The OTP code to check for existence.
     * @return {@code true} if an OTP with the code exists, otherwise {@code false}.
     */
    boolean existsByCode(String code);
}
