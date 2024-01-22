package andre.chamis.healthproject.domain.user.dto;

import java.util.Optional;

/**
 * Data Transfer Object (DTO) for creating a new user.
 *
 * @param stripeClientId is passed internally from stripe webhook, do not expect it from register api
 */
public record CreateUserDTO(
        String email,
        Optional<String> stripeClientId
) {
}
