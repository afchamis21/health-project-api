package andre.chamis.healthproject.service;

import andre.chamis.healthproject.domain.exception.BadArgumentException;
import andre.chamis.healthproject.domain.exception.EntityNotFoundException;
import andre.chamis.healthproject.domain.exception.ForbiddenException;
import andre.chamis.healthproject.domain.user.dto.CreateUserDTO;
import andre.chamis.healthproject.domain.user.dto.GetUserDTO;
import andre.chamis.healthproject.domain.user.dto.LoginDTO;
import andre.chamis.healthproject.domain.user.dto.UpdateUserDTO;
import andre.chamis.healthproject.domain.user.model.User;
import andre.chamis.healthproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service class responsible for managing user-related operations.
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final SessionService sessionService;
    private final RefreshTokenService refreshTokenService;
    private final OtpService otpService;

    /**
     * Creates a new user based on the provided {@link CreateUserDTO}.
     *
     * @param createUserDTO The DTO containing user creation information.
     * @return The created user.
     * @throws BadArgumentException If the email is invalid or already exists in the system.
     */
    private User createUser(CreateUserDTO createUserDTO) {
        User user = new User();

        if (!isEmailValid(createUserDTO.email())) {
            throw new BadArgumentException("Email inválido!");
        }

        if (userRepository.existsByEmail(createUserDTO.email())) {
            throw new BadArgumentException("O email " + createUserDTO.email() + " já está cadastrado");
        }

        user.setEmail(createUserDTO.email());

        user.setCreateDt(Date.from(Instant.now()));

        user.setRegistrationComplete(false);
        user.setPaid(false);

        return userRepository.save(user);
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
        return password.length() > 6 && matcher.matches();
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

        if (username.length() <= 3) {
            return false;
        }

        String usernameRegex = "^[A-Za-z0-9_-]+$";
        Pattern pattern = Pattern.compile(usernameRegex);
        Matcher matcher = pattern.matcher(username);

        return matcher.matches();
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
        Optional<User> userOptional = userRepository.findUserByUsername(loginDTO.username());

        if (userOptional.isEmpty()) {
            return Optional.empty();
        }
        User user = userOptional.get();

        if (!user.isRegistrationComplete()) {
            throw new ForbiddenException("Você ainda não completou seu cadastro! Siga as instruções em seu email");
        }

        if (!user.isPaid()) {
            throw new ForbiddenException("Parece que seu pagamento está atrasado!");
        }

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
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
        return userOptional.orElseThrow(() -> new ForbiddenException("Nenhum usuário logado!"));
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
     * @throws EntityNotFoundException If the user is not found.
     */
    public GetUserDTO getUserById(Optional<Long> userIdOptional) {
        Long userId = userIdOptional.orElse(sessionService.getCurrentUserId());
        Optional<User> userOptional = findUserById(userId);
        User user = userOptional.orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado para o id: " + userId, HttpStatus.BAD_REQUEST));

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
                throw new BadArgumentException("Nome de usuário inválido!");
            }
            user.setUsername(updateUserDTO.username());
            updated = true;
            needsReauthentication = true;
        }

        if (updateUserDTO.email() != null) {
            if (!isEmailValid(updateUserDTO.email())) {
                throw new BadArgumentException("Email inválido!");
            }

            user.setEmail(updateUserDTO.email());
            updated = true;
        }

        if (updateUserDTO.password() != null) {
            if (!isPasswordValid(updateUserDTO.password())) {
                throw new BadArgumentException("Senha inválida!");
            }
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = bCryptPasswordEncoder.encode(updateUserDTO.password());
            user.setPassword(hashedPassword);
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
     * Handles the registration of a new user.
     *
     * @param createUserDTO The DTO containing user creation information.
     * @return A DTO representing the registered user.
     */
    public GetUserDTO handleRegisterUser(CreateUserDTO createUserDTO) {
        User newUser = createUser(createUserDTO);

        otpService.handleCreateOTP(newUser);

        return GetUserDTO.fromUser(newUser);
    }
}
