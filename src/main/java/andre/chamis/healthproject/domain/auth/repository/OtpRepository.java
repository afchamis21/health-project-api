package andre.chamis.healthproject.domain.auth.repository;

import andre.chamis.healthproject.domain.auth.model.OTP;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * Repository class for managing OTP (One-Time Password) entities using JPA.
 */
@Repository
@RequiredArgsConstructor
public class OtpRepository {
    private final OtpJpaRepository otpJpaRepository;

    /**
     * Saves an OTP entity.
     *
     * @param otp The OTP entity to be saved.
     * @return The saved OTP entity.
     */
    public OTP save(OTP otp) {
        return otpJpaRepository.save(otp);
    }

    /**
     * Checks if an OTP exists for the specified user ID.
     *
     * @param userId The ID of the user to check for an existing OTP.
     * @return {@code true} if an OTP exists for the user, otherwise {@code false}.
     */
    public boolean existsByUserId(Long userId) {
        return otpJpaRepository.existsByUserId(userId);
    }

    /**
     * Checks if an OTP with the specified code exists.
     *
     * @param code The OTP code to check for existence.
     * @return {@code true} if an OTP with the code exists, otherwise {@code false}.
     */
    public boolean existsByCode(String code) {
        return otpJpaRepository.existsByCode(code);
    }
}
