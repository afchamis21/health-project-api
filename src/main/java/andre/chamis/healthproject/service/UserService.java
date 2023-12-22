package andre.chamis.healthproject.service;

import andre.chamis.healthproject.domain.exception.BadArgumentException;
import andre.chamis.healthproject.domain.exception.ForbiddenException;
import andre.chamis.healthproject.domain.response.ErrorMessage;
import andre.chamis.healthproject.domain.user.dto.*;
import andre.chamis.healthproject.domain.user.model.User;
import andre.chamis.healthproject.domain.user.repository.UserRepository;
import andre.chamis.healthproject.util.DateUtils;
import andre.chamis.healthproject.util.ObjectUtils;
import andre.chamis.healthproject.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service class responsible for managing user-related operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final SessionService sessionService;
    private final RefreshTokenService refreshTokenService;
    private final EmailService emailService;

    private final int OTP_LENGTH = 6;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();


    /**
     * Creates a new user based on the provided {@link CreateUserDTO}.
     *
     * @param createUserDTO The DTO containing user creation information.
     * @return The DTO containing created user.
     * @throws BadArgumentException If the email is invalid or already exists in the system.
     */
    public GetUserDTO handleRegisterUser(CreateUserDTO createUserDTO) {
        User user = new User();

        if (!isEmailValid(createUserDTO.email())) {
            throw new BadArgumentException(ErrorMessage.INVALID_EMAIL);
        }

        if (userRepository.existsByEmail(createUserDTO.email())) {
            throw new BadArgumentException(ErrorMessage.EMAIL_ALREADY_REGISTERED);
        }

        user.setEmail(createUserDTO.email());

        user.setCreateDt(Date.from(Instant.now()));
        user.setRegistrationComplete(false);
        user.setActive(false);

        String oneTimePassword = StringUtils.generateRandomString(OTP_LENGTH);
        String hashedPassword = bCryptPasswordEncoder.encode(oneTimePassword);
        user.setPassword(hashedPassword);

        String emailMessage = """
                Olá aqui está a sua senha provisória para continuar o cadastro em nosso site!
                                
                {{OTP}}
                           
                Você será solicitado a mudar sua senha e atualizar as informações quando fizer login pela primeira vez!
                """.replace("{{OTP}}", oneTimePassword);

        emailService.sendMail(user.getEmail(), emailMessage, "Complete seu cadastro");

        return GetUserDTO.fromUser(userRepository.save(user));
    }

    /**
     * Validates the format of a password. Must be non-space and at least 6 characters long.
     *
     * @param password The password to validate.
     * @return True if the password is valid, otherwise false.
     */
    private boolean isPasswordValid(String password) {
        if (password == null) {
            return false;
        }

        String passwordRegex = "^\\S+$";
        Pattern pattern = Pattern.compile(passwordRegex);
        Matcher matcher = pattern.matcher(password);
        return password.length() >= 7 && matcher.matches();
    }

    /**
     * Validates the format of a username. Alphanumeric, hyphen, and underscore, at least 4 characters long.
     *
     * @param username The username to validate.
     * @return True if the username is valid, otherwise false.
     */
    private boolean isUsernameValid(String username) {
        if (username == null) {
            return false;
        }

        String usernameRegex = "^[A-Za-z0-9_-]+$";
        Pattern pattern = Pattern.compile(usernameRegex);
        Matcher matcher = pattern.matcher(username);

        return username.length() > 4 && matcher.matches();
    }

    /**
     * Validates the format of an email. Valid email format.
     *
     * @param email The email to validate.
     * @return True if the email is valid, otherwise false.
     */
    private boolean isEmailValid(String email) {
        if (email == null) {
            return false;
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,4}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * Validates user credentials during login.
     *
     * @param loginDTO The DTO containing user login credentials.
     * @return An optional User object if credentials are valid, otherwise empty.
     * @throws ForbiddenException If the user's registration is incomplete or payment is overdue.
     */
    public Optional<User> validateUserCredential(LoginDTO loginDTO) {
        Optional<User> userOptional = userRepository.findUserByEmail(loginDTO.email());

        if (userOptional.isEmpty()) {
            return Optional.empty();
        }
        User user = userOptional.get();

        boolean isPasswordCorrect = bCryptPasswordEncoder.matches(loginDTO.password(), user.getPassword());

        return isPasswordCorrect ? Optional.of(user) : Optional.empty();
    }

    /**
     * Finds the currently logged-in user.
     *
     * @return The currently logged-in user.
     * @throws ForbiddenException If there is no current user.
     */
    public User findCurrentUser() {
        Long currentUserId = sessionService.getCurrentUserId();
        Optional<User> userOptional = findUserById(currentUserId);
        return userOptional.orElseThrow(ForbiddenException::new);
    }

    /**
     * Finds a user by their ID.
     *
     * @param userId The ID of the user to find.
     * @return An optional User object with the given ID, otherwise empty.
     */
    public Optional<User> findUserById(Long userId) {
        return userRepository.findById(userId);
    }

    /**
     * Retrieves user information by ID, or the current user if no ID is provided.
     *
     * @param userIdOptional Optional ID of the user to retrieve information for.
     * @return A DTO representing the user's information.
     * @throws BadArgumentException If the user is not found.
     */
    public GetUserDTO getUserById(Optional<Long> userIdOptional) {
        Long userId = userIdOptional.orElse(sessionService.getCurrentUserId());
        Optional<User> userOptional = findUserById(userId);
        User user = userOptional.orElseThrow(() -> new BadArgumentException(ErrorMessage.USER_NOT_FOUND));

        return GetUserDTO.fromUser(user);
    }

    /**
     * Updates user information based on the provided {@link UpdateUserDTO}.
     *
     * @param updateUserDTO The DTO containing updated user information.
     * @return A DTO representing the updated user information.
     * @throws BadArgumentException If any provided information is invalid (username, email, or password).
     */
    public GetUserDTO updateUser(UpdateUserDTO updateUserDTO) {
        boolean updated = false;
        boolean needsReauthentication = false;
        User user = findCurrentUser();

        String username = user.getUsername();

        if (updateUserDTO.username() != null) {
            if (!isUsernameValid(updateUserDTO.username())) {
                throw new BadArgumentException(ErrorMessage.INVALID_USERNAME);
            }
            user.setUsername(updateUserDTO.username());
            updated = true;
            needsReauthentication = true;
        }

        if (updateUserDTO.email() != null) {
            if (!isEmailValid(updateUserDTO.email())) {
                throw new BadArgumentException(ErrorMessage.INVALID_EMAIL);
            }

            user.setEmail(updateUserDTO.email());
            updated = true;
        }

        if (updateUserDTO.password() != null) {
            setUserPassword(user, updateUserDTO.password(), updateUserDTO.confirmPassword());
            updated = true;
            needsReauthentication = true;
        }

        if (updated) {
            user = userRepository.save(user);
        }

        if (needsReauthentication) {
            sessionService.deleteCurrentSession();
            refreshTokenService.deleteTokenByUsername(username);
        }

        return GetUserDTO.fromUser(user);
    }

    /**
     * Handles the completion of user registration.
     *
     * @param completeRegistrationDTO The DTO containing information for completing the user registration.
     * @return A DTO representing the user after completing the registration.
     * @throws BadArgumentException If the provided information is incomplete or invalid.
     */
    public GetUserDTO handleCompleteRegistration(CompleteRegistrationDTO completeRegistrationDTO) {
        if (ObjectUtils.areAnyPropertiesNull(completeRegistrationDTO)) {
            throw new BadArgumentException(ErrorMessage.MISSING_INFORMATION);
        }

        User user = findCurrentUser();

        if (!isUsernameValid(completeRegistrationDTO.username())) {
            throw new BadArgumentException(ErrorMessage.INVALID_USERNAME);
        }
        user.setUsername(completeRegistrationDTO.username());

        setUserPassword(user, completeRegistrationDTO.password(), completeRegistrationDTO.confirmPassword());

        String username = user.getUsername();
        sessionService.deleteCurrentSession();
        refreshTokenService.deleteTokenByUsername(username);

        user.setRegistrationComplete(true);
        // TODO activar o usuario quando pagar

        return GetUserDTO.fromUser(userRepository.save(user));
    }

    /**
     * Sets the password for a user and validates its strength.
     *
     * @param user            The user for whom the password is being set.
     * @param password        The new password.
     * @param confirmPassword The confirmation of the new password.
     * @throws BadArgumentException If the password is invalid or the confirmation doesn't match.
     */
    private void setUserPassword(User user, String password, String confirmPassword) {
        if (!isPasswordValid(password)) {
            throw new BadArgumentException(ErrorMessage.INVALID_PASSWORD);
        }

        if (!password.equals(confirmPassword)) {
            throw new BadArgumentException(ErrorMessage.PASSWORDS_DONT_MATCH);
        }

        String hashedPassword = bCryptPasswordEncoder.encode(password);
        user.setPassword(hashedPassword);
    }


    /**
     * Activates a user with the given user ID.
     *
     * @param userId The ID of the user to activate.
     * @return A DTO representing the activated user.
     * @throws BadArgumentException If the user is not found.
     */
    public GetUserDTO activateUser(Long userId) {
        Optional<User> result = findUserById(userId);
        User user = result.orElseThrow(() -> new BadArgumentException(ErrorMessage.USER_NOT_FOUND));
        user.setActive(true);
        return GetUserDTO.fromUser(userRepository.save(user));
    }

    /**
     * Deactivates a user with the given user ID.
     *
     * @param userId The ID of the user to deactivate.
     * @return A DTO representing the deactivated user.
     * @throws BadArgumentException If the user is not found.
     */
    public GetUserDTO deactivateUser(Long userId) {
        Optional<User> result = findUserById(userId);
        User user = result.orElseThrow(() -> new BadArgumentException(ErrorMessage.USER_NOT_FOUND));
        user.setActive(false);
        return GetUserDTO.fromUser(userRepository.save(user));
    }

    public void deleteUser(Long userId) {
        userRepository.delete(userId);
    }

    /**
     * Updates passwords for users with expired and incomplete registrations.
     * Generates a new OTP, encrypts it, updates the password, and sends an email to the user.
     *
     * @return The number of users whose passwords were updated.
     */
    public int updatePasswordsForIncompleteRegistrations() {
        Date oneWeekAgo = DateUtils.calculateOneWeekAgo();
        List<User> users = userRepository.findAllWithExpiredIncompleteRegistrations(oneWeekAgo);

        users.forEach(user -> {
            String newOTP = StringUtils.generateRandomString(OTP_LENGTH);
            String hashedPassword = bCryptPasswordEncoder.encode(newOTP);
            user.setPassword(hashedPassword);

            userRepository.save(user);

            String emailMessage = """
                    Já que você demorou para realizar seu cadastro, tivemos que mudar sua senha provisória ;(
                    Sua nova senha é:
                                        
                    {{OTP}}
                                        
                    Clique aqui para concluir seu cadastro
                    """.replace("{{OTP}}", newOTP);

            emailService.sendMail(user.getEmail(), emailMessage, "Conclua seu cadastro!");
        });

        return users.size();
    }
}
