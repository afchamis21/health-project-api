package andre.chamis.healthproject.domain.auth.dto;


import andre.chamis.healthproject.domain.user.dto.GetUserDTO;
import andre.chamis.healthproject.domain.user.model.User;

/**
 * Data Transfer Object (DTO) for tokens used in authentication.
 */
public record TokensDTO(
        String accessToken,
        String refreshToken,
        GetUserDTO user
) {
    public TokensDTO(String accessToken, String refreshToken, User user) {
        this(accessToken, refreshToken, GetUserDTO.fromUser(user));
    }
}
