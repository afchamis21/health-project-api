package andre.chamis.healthproject.domain.user.dto;

/**
 * Data Transfer Object (DTO) for completing a user registration.
 */
public record CompleteRegistrationDTO(
        String username,
        String email,
        String password,
        String confirmPassword
) {
}
