package andre.chamis.healthproject.domain.user.dto;

import andre.chamis.healthproject.domain.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for retrieving user information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetUserDTO {
    /**
     * The unique identifier of the user.
     */
    private Long userId;

    /**
     * The username of the user.
     */
    private String username;

    /**
     * The email address of the user.
     */
    private String email;

    /**
     * Indicates whether the user's registration is complete.
     */
    private Boolean isRegistrationComplete;

    /**
     * Indicates whether the user has paid.
     */
    private Boolean isPaymentActive;

    private Boolean isCustomer;

    /**
     * Creates a {@link GetUserDTO} instance from a {@link User} object.
     *
     * @param user The {@link User} object to convert.
     * @return A {@link GetUserDTO} instance containing user information.
     */
    public static GetUserDTO fromUser(User user) {
        GetUserDTO getUserDTO = new GetUserDTO();
        getUserDTO.setUserId(user.getUserId());
        getUserDTO.setUsername(user.getUsername());
        getUserDTO.setEmail(user.getEmail());
        getUserDTO.setIsRegistrationComplete(user.isRegistrationComplete());
        getUserDTO.setIsPaymentActive(user.isPaymentActive());
        getUserDTO.setIsCustomer(null != user.getStripeClientId());

        return getUserDTO;
    }
}
