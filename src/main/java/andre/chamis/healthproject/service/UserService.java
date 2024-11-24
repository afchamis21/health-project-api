package andre.chamis.healthproject.service;

import andre.chamis.healthproject.context.ServiceContext;
import andre.chamis.healthproject.domain.health.attendance.dto.GetAttendanceDTO;
import andre.chamis.healthproject.domain.health.collaborator.dto.CreateCollaboratorDTO;
import andre.chamis.healthproject.domain.health.collaborator.dto.GetCollaboratorDTO;
import andre.chamis.healthproject.domain.health.patient.dto.CreatePatientDTO;
import andre.chamis.healthproject.domain.health.patient.dto.GetPatientSummaryDTO;
import andre.chamis.healthproject.domain.payment.dto.GetIsUserSubscriberResponse;
import andre.chamis.healthproject.domain.user.dto.*;
import andre.chamis.healthproject.domain.user.model.User;
import andre.chamis.healthproject.domain.user.repository.UserRepository;
import andre.chamis.healthproject.exception.BadArgumentException;
import andre.chamis.healthproject.exception.ForbiddenException;
import andre.chamis.healthproject.exception.InternalServerException;
import andre.chamis.healthproject.exception.ValidationException;
import andre.chamis.healthproject.infra.request.request.PaginationInfo;
import andre.chamis.healthproject.infra.request.response.ErrorMessage;
import andre.chamis.healthproject.infra.request.response.PaginatedResponse;
import andre.chamis.healthproject.util.DateUtils;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Subscription;
import com.stripe.param.CustomerUpdateParams;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

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
    private final PatientService patientService;
    private final RefreshTokenService refreshTokenService;
    private final UserSubscriptionService subscriptionService;
    private final AttendanceService attendanceService;
    private final CollaboratorService collaboratorService;

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
        try {
            log.debug("Checking if user is already registered with email [{}]", email);
            if (userRepository.existsByEmail(email)) {
                log.error("User is already registered with email [{}]", email);
                throw new BadArgumentException(ErrorMessage.USER_ALREADY_REGISTERED);
            }
            User user;
            if (stripeClientId.isPresent()) {
                user = new User(email, stripeClientId.get());
            } else {
                user = new User(email);
            }

            log.debug("User instantiated [{}]", user);

            String otp = user.setOtp(OTP_LENGTH);

            log.debug("Generated OTP for user [{}]", user.getEmail());

            user = userRepository.save(user);

            log.info("User [{}] saved to database", user.getEmail());

            String emailMessage = """
                    Olá aqui está a sua senha provisória para continuar o cadastro em nosso site!
                                        
                    {{OTP}}
                                        
                    Você será solicitado a mudar sua senha e atualizar as informações quando fizer login pela primeira vez!
                    """.replace("{{OTP}}", otp);

            emailService.sendSimpleMail(user.getEmail(), emailMessage, "Complete seu cadastro");

            log.info("OTP Email sent to user [{}]", user.getEmail());

            return user;
        } catch (ValidationException ex) {
            throw new BadArgumentException(ex.getError());
        }
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
     * Retrieves a User object by its ID.
     *
     * @param userId The ID of the user to retrieve.
     * @return The User object corresponding to the given ID.
     * @throws BadArgumentException If no user is found with the provided ID.
     */
    private User getUserById(Long userId) {
        return findUserById(userId).orElseThrow(
                () -> new BadArgumentException(ErrorMessage.USER_NOT_FOUND)
        );
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
        User user = getUserById(userId);
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
    public UpdateUserResponse updateUser(UpdateUserDTO updateUserDTO) {
        try {
            boolean updated = false;
            boolean needsReAuthentication = false;
            User user = findCurrentUser();

            log.info("Updating user [{}] with payload [{}]", user.getEmail(), updateUserDTO);

            String username = user.getUsername();

            if (updateUserDTO.username() != null
                    && !updateUserDTO.username().isBlank()
                    && !updateUserDTO.username().equals(username)
            ) {
                user.setUsername(updateUserDTO.username());
                updated = true;
            }

            if (updateUserDTO.password() != null && !updateUserDTO.password().isBlank()) {
                user.setPassword(updateUserDTO.password(), updateUserDTO.confirmPassword());
                updated = true;
                needsReAuthentication = true;
                log.debug("User [{}] updated password", user.getEmail());
            }

            if (updateUserDTO.email() != null
                    && !updateUserDTO.email().isBlank()
                    && !updateUserDTO.email().equals(user.getEmail())
            ) {
                if (userRepository.existsByEmail(updateUserDTO.email())) {
                    throw new BadArgumentException(ErrorMessage.USER_ALREADY_REGISTERED);
                }

                user.setEmail(updateUserDTO.email());
                updated = true;
                needsReAuthentication = true;

                log.info("User updated email! Updating data on Stripe");

                Customer customer = Customer.retrieve(user.getStripeClientId());
                CustomerUpdateParams params = CustomerUpdateParams.builder()
                        .setEmail(updateUserDTO.email())
                        .build();

                customer.update(params);

                log.info("Stripe data updated!");
            }

            log.debug("Was user updated [{}]", updated);

            if (updated) {
                user = userRepository.save(user);
            }

            log.debug("User needs to be re-authenticated [{}]", needsReAuthentication);

            if (needsReAuthentication) {
                sessionService.deleteAllUserSessions();
                refreshTokenService.deleteTokenByUsername(username);
            }

            return new UpdateUserResponse(needsReAuthentication);
        } catch (ValidationException e) {
            throw new BadArgumentException(e.getError());
        } catch (StripeException e) {
            log.error("Error updating stripe data", e);
            throw new InternalServerException(ErrorMessage.INTERNAL_SERVER_ERROR);
        }
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
        try {
            User user = findCurrentUser();

            log.info("Attempting to complete registration for user [{}] with payload [{}]", user.getUserId(), completeRegistrationDTO);

            user.completeRegistration(completeRegistrationDTO);

            String username = user.getUsername();
            sessionService.deleteCurrentSession();
            refreshTokenService.deleteTokenByUsername(username);

            log.info("User [{}] successfully activated", user.getEmail());

            return GetUserDTO.fromUser(userRepository.save(user));
        } catch (ValidationException ex) {
            throw new BadArgumentException(ex.getError());
        }
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

        User user = getUserById(userId);

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

        User user = getUserById(userId);

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
            String newOTP = user.setOtp(OTP_LENGTH);

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

    /**
     * Retrieves information about whether a user is a subscriber based on their email.
     *
     * @param email The email of the user.
     * @return A response indicating if the user is a subscriber.
     */
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

    /**
     * Handles a payment failure event for a user by updating their payment status and sending an email notification.
     *
     * @param email The email of the user.
     */
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

    /**
     * Handles the deletion of a subscription, updates the user's payment status, and sends an email notification.
     *
     * @param subscription The subscription that was deleted.
     */
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

    /**
     * Handles the creation of a subscription.
     *
     * @param subscription The subscription that was created.
     */
    public void handleSubscriptionCreated(Subscription subscription) {
        subscriptionService.createSubscription(subscription);
    }

    /**
     * Handles the update of a subscription.
     *
     * @param subscription The subscription that was updated.
     */
    public void handleSubscriptionUpdated(Subscription subscription) {
        subscriptionService.updateSubscription(subscription);
    }

    /**
     * Retrieves a user if they are a customer based on their email.
     *
     * @param email The email of the user.
     * @return An optional containing the user if they are a customer, otherwise empty.
     */
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

    public GetPatientSummaryDTO addPatient(CreatePatientDTO createPatientDTO) {
        return patientService.createPatient(createPatientDTO);
    }

    public PaginatedResponse<GetPatientSummaryDTO> searchPatientByNameAndCollaboratorId(String name, PaginationInfo paginationInfo) {
        Long currentUserId = ServiceContext.getContext().getUserId();

        if (name == null || name.isBlank()) {
            return patientService.findPatientsByCollaboratorId(currentUserId, paginationInfo);
        }

        return patientService.searchPatientsByNameAndCollaboratorId(ServiceContext.getContext().getUserId(), name, paginationInfo);
    }

    /**
     * Finds a user by their email.
     *
     * @param email The email of the user to find.
     * @return An optional containing the found user, or empty if not found.
     */
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public GetAttendanceDTO clockIn(Long patientId) {
        boolean isUserCollaboratorOfPatient = collaboratorService.isUserActiveCollaboratorOfPatient(
                patientId,
                ServiceContext.getContext().getUserId()
        );

        if (!isUserCollaboratorOfPatient) {
            throw new ForbiddenException(ErrorMessage.INVALID_PATIENT_ACCESS);
        }

        User user = findCurrentUser();

        log.info("Clocking in user at [{}]", patientId);

        if (user.getClockedIn() != null) {
            attendanceService.clockOut(user.getUserId());
        }

        user.setClockedIn(true);
        user.setClockedInAt(patientId);

        userRepository.save(user);

        return attendanceService.clockIn(patientId, user.getUserId());
    }

    public List<GetAttendanceDTO> clockOut() {
        User user = findCurrentUser();

        user.setClockedIn(false);
        user.setClockedInAt(null);

        userRepository.save(user);

        return attendanceService.clockOut(user.getUserId());
    }

    public GetCollaboratorDTO addCollaboratorToPatient(CreateCollaboratorDTO createCollaboratorDTO) {
        String collaboratorEmail = createCollaboratorDTO.email();

        log.info("Getting user with email [{}] or registering a new one!", collaboratorEmail);

        User collaborator = findUserByEmail(collaboratorEmail).orElseGet(() -> {
            log.warn("Creating a new user with email [{}]", collaboratorEmail);
            return createUser(collaboratorEmail, Optional.empty());
        });

        return patientService.addCollaborator(collaborator, createCollaboratorDTO.patientId(), createCollaboratorDTO.description());
    }

    public boolean existsByEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }

        return userRepository.existsByEmail(email);
    }
}
