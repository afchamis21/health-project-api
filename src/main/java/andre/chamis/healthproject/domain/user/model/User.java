package andre.chamis.healthproject.domain.user.model;

import andre.chamis.healthproject.domain.user.dto.CompleteRegistrationDTO;
import andre.chamis.healthproject.exception.BadArgumentException;
import andre.chamis.healthproject.exception.ValidationException;
import andre.chamis.healthproject.infra.request.response.ErrorMessage;
import andre.chamis.healthproject.util.StringUtils;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Instant;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a user in the system.
 */
@Getter
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User {
    /**
     * The unique identifier of the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Long userId;

    /**
     * The Stripe client ID associated with the user.
     */
    @Setter
    @Column(name = "stripe_client_id")
    private String stripeClientId;

    /**
     * The unique username of the user.
     */
    private String username;

    /**
     * The email address of the user.
     */
    @Column(unique = true)
    private String email;

    /**
     * The password of the user.
     */
    private String password;

    /**
     * Indicates whether the user registration is complete or not.
     */
    @Column(name = "is_registration_complete")
    private boolean isRegistrationComplete;

    /**
     * Indicates whether the user is active or not.
     */
    @Column(name = "is_active")
    private boolean isActive;

    /**
     * Indicates whether the user has paid or not.
     */
    @Column(name = "is_payment_active")
    private boolean isPaymentActive;

    /**
     * Indicates whether the user is currently clocked in or not.
     */
    @Setter
    @Column(name = "is_clocked_in")
    private Boolean clockedIn;

    /**
     * The timestamp when the user was last clocked in.
     */
    @Setter
    @Column(name = "clocked_in_at")
    private Long clockedInAt;

    /**
     * The creation date of the user's record.
     */
    @Setter
    @Column(name = "create_dt")
    private Date createDt;

    /**
     * The update date of the user's record.
     */
    @Setter
    @Column(name = "update_dt")
    private Date updateDt;

    public User(String email) throws ValidationException {
        setEmail(email);
        setCreateDt(Date.from(Instant.now()));
        setRegistrationComplete(false);
        setPaymentActive(false);
        setActive(false);

        this.email = email;
    }

    public User(String email, String stripeClientId) throws ValidationException {
        this(email);

        this.stripeClientId = stripeClientId;
    }

    public void setActive(boolean b) {
        this.isActive = b;
    }

    public void setPaymentActive(boolean b) {
        this.isPaymentActive = b;
    }

    public void setRegistrationComplete(boolean b) {
        this.isRegistrationComplete = b;
    }

    public void setUsername(String username) throws ValidationException {
        validateUsername(username);

        this.username = username;
    }

    public void setEmail(String email) throws ValidationException {
        validateEmail(email);

        this.email = email;
    }

    /**
     * Validates the format of an email. Valid email format.
     *
     * @param email The email to validate.
     */
    private void validateEmail(String email) throws ValidationException {
        if (email == null) {
            throw new ValidationException(ErrorMessage.INVALID_EMAIL);
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,4}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            throw new ValidationException(ErrorMessage.INVALID_EMAIL);
        }
    }

    /**
     * Validates the format of a username. Alphanumeric, hyphen, and underscore, at least 4 characters long.
     *
     * @param username The username to validate.
     */
    private void validateUsername(String username) throws ValidationException {
        if (username == null) {
            throw new ValidationException(ErrorMessage.INVALID_USERNAME);
        }

        String usernameRegex = "^[A-Za-z0-9_ -]+$";
        Pattern pattern = Pattern.compile(usernameRegex);
        Matcher matcher = pattern.matcher(username);

        if (username.length() <= 4 || !matcher.matches()) {
            throw new ValidationException(ErrorMessage.INVALID_USERNAME);
        }
    }

    public String setOtp(int otpLength) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String oneTimePassword = StringUtils.generateRandomString(otpLength);
        this.password = bCryptPasswordEncoder.encode(oneTimePassword);

        return oneTimePassword;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", stripeClientId='" + stripeClientId + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", isRegistrationComplete=" + isRegistrationComplete +
                ", isActive=" + isActive +
                ", isPaymentActive=" + isPaymentActive +
                ", createDt=" + createDt +
                ", updateDt=" + updateDt +
                '}';
    }

    public void setPassword(String password, String confirmPassword) throws ValidationException {
        validatePassword(password);

        if (!password.equals(confirmPassword)) {
            throw new BadArgumentException(ErrorMessage.PASSWORDS_DONT_MATCH);
        }

        setHashedPassword(password);
    }

    private void setHashedPassword(String password) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(10);
        this.password = bCryptPasswordEncoder.encode(password);
    }

    /**
     * Validates the format of a password. Must be non-space and at least 6 characters long.
     *
     * @param password The password to validate.
     */
    private void validatePassword(String password) throws ValidationException {
        if (password == null) {
            throw new ValidationException(ErrorMessage.INVALID_PASSWORD);
        }

        if (password.length() < 8) {
            throw new ValidationException(ErrorMessage.INVALID_PASSWORD);
        }

        if (!StringUtils.containsUpperCaseLetters(password)) {
            throw new ValidationException(ErrorMessage.INVALID_PASSWORD);
        }

        if (!StringUtils.containsLowerCaseLetters(password)) {
            throw new ValidationException(ErrorMessage.INVALID_PASSWORD);
        }

        if (!StringUtils.containsDigits(password)) {
            throw new ValidationException(ErrorMessage.INVALID_PASSWORD);
        }

        if (!StringUtils.containsSpecialChars(password)) {
            throw new ValidationException(ErrorMessage.INVALID_PASSWORD);
        }
    }

    public void completeRegistration(CompleteRegistrationDTO completeRegistrationDTO) throws ValidationException {
        setUsername(completeRegistrationDTO.username());
        setPassword(completeRegistrationDTO.password(), completeRegistrationDTO.confirmPassword());

        setRegistrationComplete(true);
        setActive(true);
    }
}
