package andre.chamis.healthproject.service;

import andre.chamis.healthproject.context.ServiceContext;
import andre.chamis.healthproject.domain.exception.BadArgumentException;
import andre.chamis.healthproject.domain.exception.ForbiddenException;
import andre.chamis.healthproject.domain.payment.dto.GetIsUserSubscriberResponse;
import andre.chamis.healthproject.domain.request.PaginationInfo;
import andre.chamis.healthproject.domain.response.ErrorMessage;
import andre.chamis.healthproject.domain.response.PaginatedResponse;
import andre.chamis.healthproject.domain.user.dto.*;
import andre.chamis.healthproject.domain.user.model.User;
import andre.chamis.healthproject.domain.user.repository.UserRepository;
import andre.chamis.healthproject.domain.workspace.dto.GetWorkspaceDTO;
import andre.chamis.healthproject.domain.workspace.repository.WorkspaceRepository;
import andre.chamis.healthproject.util.DateUtils;
import andre.chamis.healthproject.util.ObjectUtils;
import andre.chamis.healthproject.util.StringUtils;
import com.stripe.model.Subscription;
import jakarta.transaction.Transactional;
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
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final SessionService sessionService;
    private final WorkspaceRepository workspaceRepository;
    private final RefreshTokenService refreshTokenService;
    private final UserSubscriptionService subscriptionService;

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
        log.info("Attempting to register a new User with payload [{}]", createUserDTO);
        return GetUserDTO.fromUser(createUser(createUserDTO.email(), Optional.empty()));
    }

    /**
     * Handles user registration from the Stripe platform.
     * If the user with the provided email exists, attaches the Stripe client ID.
     * If not, creates a new user with the given email and attaches the Stripe client ID.
     *
     * @param email          The email of the user.
     * @param stripeClientId The Stripe client ID to be attached.
     */
    public void handleRegisterUserFromStripe(String email, String stripeClientId) {
        if (userRepository.existsByEmail(email)) {
            attachStripeClientToUser(email, stripeClientId);
            return;
        }

        createUser(email, Optional.of(stripeClientId));
    }

    /**
     * Attaches a Stripe client ID to an existing user or creates a new user with the provided email and Stripe client ID.
     *
     * @param email          The email of the user.
     * @param stripeClientId The Stripe client ID to be attached.
     */
    private void attachStripeClientToUser(String email, String stripeClientId) {
        Optional<User> result = userRepository.findUserByEmail(email);
        if (result.isEmpty()) {
            createUser(email, Optional.of(stripeClientId));
            return;
        }

        User user = result.get();
        user.setStripeClientId(stripeClientId);
        userRepository.save(user);
    }

    /**
     * Creates a new user with the provided email and optional Stripe client ID.
     *
     * @param email          The email of the user.
     * @param stripeClientId The optional Stripe client ID to be attached.
     * @return The created user entity.
     */
    public User createUser(String email, Optional<String> stripeClientId) {
        if (!isEmailValid(email)) {
            throw new BadArgumentException(ErrorMessage.INVALID_EMAIL);
        }

        log.debug("Checking if user is already registered with email [{}]", email);
        if (userRepository.existsByEmail(email)) {
            log.error("User is already registered with email [{}]", email);
            throw new BadArgumentException(ErrorMessage.USER_ALREADY_REGISTERED);
        }

        User user = new User();

        user.setEmail(email);
        user.setUsername(email);
        user.setCreateDt(Date.from(Instant.now()));
        user.setRegistrationComplete(false);
        user.setPaymentActive(false);
        user.setActive(false);

        stripeClientId.ifPresent(user::setStripeClientId);

        log.debug("User instantiated [{}]", user);

        String oneTimePassword = StringUtils.generateRandomString(OTP_LENGTH);
        String hashedPassword = bCryptPasswordEncoder.encode(oneTimePassword);
        user.setPassword(hashedPassword);

        log.debug("Generated OTP for user [{}]", user.getEmail());

        user = userRepository.save(user);

        log.info("User [{}] saved to database", user.getEmail());

        String emailMessage = """
                Olá aqui está a sua senha provisória para continuar o cadastro em nosso site!
                                
                {{OTP}}
                           
                Você será solicitado a mudar sua senha e atualizar as informações quando fizer login pela primeira vez!
                """.replace("{{OTP}}", oneTimePassword);

        emailService.sendSimpleMail(user.getEmail(), emailMessage, "Complete seu cadastro");

        log.info("OTP Email sent to user [{}]", user.getEmail());

        return user;
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

        if (password.length() < 8) {
            return false;
        }

        if (!StringUtils.containsUpperCaseLetters(password)) {
            return false;
        }

        if (!StringUtils.containsLowerCaseLetters(password)) {
            return false;
        }

        if (!StringUtils.containsDigits(password)) {
            return false;
        }

        if (!StringUtils.containsSpecialChars(password)) {
            return false;
        }

        return true;
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

        String usernameRegex = "^[A-Za-z0-9_ -]+$";
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
        log.debug("Attempting to find currentUser");
        Long currentUserId = ServiceContext.getContext().getUserId();
        log.debug("Current user id [{}]", currentUserId);
        Optional<User> userOptional = findUserById(currentUserId);
        log.info("Found [{}]", userOptional);
        return userOptional.orElseThrow(ForbiddenException::new);
    }

    /**
     * Finds a user by their ID.
     *
     * @param userId The ID of the user to find.
     * @return An optional User object with the given ID, otherwise empty.
     */
    public Optional<User> findUserById(Long userId) {
        log.info("Searching for user with id [{}]", userId);
        Optional<User> user = userRepository.findById(userId);
        log.debug("Got result from userFindById [{}]", user);
        return user;
    }

    /**
     * Retrieves user information by ID, or the current user if no ID is provided.
     *
     * @param userIdOptional Optional ID of the user to retrieve information for.
     * @return A DTO representing the user's information.
     * @throws BadArgumentException If the user is not found.
     */
    public GetUserDTO getUserById(Optional<Long> userIdOptional) {
        Long userId = userIdOptional.orElse(ServiceContext.getContext().getUserId());
        log.info("Getting user with id [{}]", userId);
        Optional<User> userOptional = findUserById(userId);
        User user = userOptional.orElseThrow(() -> new BadArgumentException(ErrorMessage.USER_NOT_FOUND));
        log.debug("Found user [{}]", user);
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

        log.info("Updating user [{}] with payload [{}]", user.getEmail(), updateUserDTO);

        String username = user.getUsername();

        if (updateUserDTO.username() != null && !updateUserDTO.username().isBlank()) {
            if (!isUsernameValid(updateUserDTO.username())) {
                throw new BadArgumentException(ErrorMessage.INVALID_USERNAME);
            }
            user.setUsername(updateUserDTO.username());
            updated = true;
            log.debug("User [{}] updated username", user.getEmail());
        }

        if (updateUserDTO.password() != null && !updateUserDTO.password().isBlank()) {
            setUserPassword(user, updateUserDTO.password(), updateUserDTO.confirmPassword());
            updated = true;
            needsReauthentication = true;
            log.debug("User [{}] updated password", user.getEmail());
        }

        log.debug("Was user updated [{}]", updated);

        if (updated) {
            user = userRepository.save(user);
        }

        log.debug("User needs to be reauthenticated [{}]", needsReauthentication);

        if (needsReauthentication) {
            sessionService.deleteAllUserSessions();
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
    @Transactional
    public GetUserDTO handleCompleteRegistration(CompleteRegistrationDTO completeRegistrationDTO) {
        if (ObjectUtils.areAnyPropertiesNull(completeRegistrationDTO)) {
            throw new BadArgumentException(ErrorMessage.MISSING_INFORMATION);
        }

        User user = findCurrentUser();

        log.info("Attempting to complete registration for user [{}] with payload [{}]", user.getUserId(), completeRegistrationDTO);

        if (!isUsernameValid(completeRegistrationDTO.username())) {
            throw new BadArgumentException(ErrorMessage.INVALID_USERNAME);
        }
        user.setUsername(completeRegistrationDTO.username());

        setUserPassword(user, completeRegistrationDTO.password(), completeRegistrationDTO.confirmPassword());

        String username = user.getUsername();
        sessionService.deleteCurrentSession();
        refreshTokenService.deleteTokenByUsername(username);

        user.setRegistrationComplete(true);
        user.setActive(true);

        log.info("User [{}] successfully activated", user.getEmail());

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
        log.info("Activating user [{}]", userId);

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
        log.info("Deactivating user [{}]", userId);

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

        log.info("Updating [{}] passwords for incomplete registrations", users.size());

        users.forEach(user -> {
            log.info("Updated password for user [{}]", user.getEmail());
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

            emailService.sendSimpleMail(user.getEmail(), emailMessage, "Conclua seu cadastro!");
            log.info("OTP Email sent to user [{}]", user.getEmail());
        });

        return users.size();
    }

    /**
     * Handles the registration of a payment for the user identified by the provided email.
     * If the user is found, the payment status is updated to active.
     *
     * @param email The email of the user for whom the payment registration is being handled.
     * @throws BadArgumentException If the user with the provided email is not found.
     */
    public void handleRegisterPayment(String email) {
        log.info("Trying to find user [{}] on database to register payment", email);
        Optional<User> result = userRepository.findUserByEmail(email);

        User user = result.orElseGet(() -> {
            log.info("User was not on database. Webhook invoice.paid must have gotten here before customer.created! Creating a new one");
            return createUser(email, Optional.empty());
        });

        user.setPaymentActive(true);
        userRepository.save(user);
    }

    public GetIsUserSubscriberResponse getIsUserSubscriber(String email) {
        log.info("Checking if user [{}] is subscribed", email);

        Optional<User> result = userRepository.findUserByEmail(email);
        if (result.isEmpty()) {
            log.info("User [{}] not found", email);
            return new GetIsUserSubscriberResponse(false);
        }

        User user = result.get();

        log.info("User [{}] is updated [{}]", email, user.isPaymentActive());

        return new GetIsUserSubscriberResponse(user.isPaymentActive());
    }

    public void handlePaymentFailed(String email) {
        log.info("Payment failed for user [{}] setting isPaymentActive false", email);


        Optional<User> result = userRepository.findUserByEmail(email);
        User user = result.orElseThrow(() -> new BadArgumentException(ErrorMessage.USER_NOT_FOUND));
        boolean wasUserPaymentActive = user.isPaymentActive();

        user.setPaymentActive(false);

        userRepository.save(user);

        if (wasUserPaymentActive) {
            emailService.sendSimpleMail(user.getEmail(), """
                            Olá, parece que tivemos um problema ao processar seu pagamento...
                                                
                            Por causa disso, vamos revogar seu acesso a plataforma. Para retomar seu acesso, acesse nosso site _aqui_, e faça o pagamento novamente.
                            """,
                    "Erro ao processar pagamento"
            );
        }
    }

    public void handleSubscriptionDeleted(Subscription subscription) {
        log.info("Subscription [{}] from user [{}] ended. Deleting and setting isPaymentActive false it!",
                subscription.getId(), subscription.getCustomer()
        );

        Optional<User> result = userRepository.findUserByStripeClientId(subscription.getCustomer());
        User user = result.orElseThrow(() -> new BadArgumentException(ErrorMessage.USER_NOT_FOUND));
        user.setPaymentActive(false);
        user.setStripeClientId(null);
        userRepository.save(user);

        subscriptionService.deleteSubscription(subscription);

        emailService.sendSimpleMail(user.getEmail(), """
                        Olá, sua assinatura para usar nosso aplicativo chegou ao fim...
                                            
                        Convidamos você a retomar sua assinatura por especial *aqui*
                        """,
                "Sua assinatura acabou"
        );
    }

    public void handleSubscriptionCreated(Subscription subscription) {
        subscriptionService.createSubscription(subscription);
    }

    public void handleSubscriptionUpdated(Subscription subscription) {
        subscriptionService.updateSubscription(subscription);
    }

    public Optional<User> getUserIfIsCustomer(String email) {
        Optional<User> result = userRepository.findUserByEmail(email);

        if (result.isEmpty()) {
            return Optional.empty();
        }

        User user = result.get();

        return null != user.getStripeClientId() && !user.getStripeClientId().isBlank()
                ? result
                : Optional.empty();
    }

    public PaginatedResponse<GetWorkspaceDTO> getUserWorkspaces(PaginationInfo paginationInfo) {
        return workspaceRepository.findWorkspacesByOwnerId(ServiceContext.getContext().getUserId(), paginationInfo);
    }

    public PaginatedResponse<GetWorkspaceDTO> searchWorkspacesByNameAndMemberId(String name, PaginationInfo paginationInfo) {
        return workspaceRepository.searchWorkspacesByNameAndMemberId(ServiceContext.getContext().getUserId(), name, paginationInfo);
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }
}
