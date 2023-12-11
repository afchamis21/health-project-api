package andre.chamis.healthproject.domain.user.dto;

/**
 * Data Transfer Object (DTO) for user login information.
 */
public record LoginDTO(
        String username,
        String password
) {
}
