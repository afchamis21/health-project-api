package andre.chamis.healthproject.scheduled;


import andre.chamis.healthproject.domain.user.repository.UserRepository;
import andre.chamis.healthproject.service.RefreshTokenService;
import andre.chamis.healthproject.service.SessionService;
import andre.chamis.healthproject.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

/**
 * Component containing scheduled tasks to perform periodic operations.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledJobs {
    private final RefreshTokenService refreshTokenService;
    private final SessionService sessionService;
    private final UserRepository userRepository;
    private final UserService userService;

    /**
     * Scheduled task to delete expired refresh tokens every day at 00:00.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void deleteExpiredRefreshTokens() {
        Instant executionStart = Instant.now();
        int deletedTokens = refreshTokenService.deleteAllExpired();
        Instant executionEnd = Instant.now();
        log.info(
                "Deleted [{} expired refresh tokens]. Execution took [{} ms]",
                deletedTokens,
                Duration.between(executionStart, executionEnd).toMillis()
        );
    }

    /**
     * Scheduled task to delete expired sessions every day at 00:00.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void deleteExpiredSessions() {
        Instant executionStart = Instant.now();
        int deletedSessions = sessionService.deleteAllExpired();
        Instant executionEnd = Instant.now();
        log.info(
                "Deleted [{} expired sessions]. Execution tool [{} ms]",
                deletedSessions,
                Duration.between(executionStart, executionEnd).toMillis()
        );
    }

    /**
     * Scheduled task to update passwords for users with expired and incomplete registrations every day at 00:00.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void updatePasswordsForIncompleteRegistrations() {
        Instant executionStart = Instant.now();
        int updatedOTPs = userService.updatePasswordsForIncompleteRegistrations();
        Instant executionEnd = Instant.now();
        log.info(
                "Updated [{} OTPs for users with incomplete registrations]. Execution tool [{} ms]",
                updatedOTPs,
                Duration.between(executionStart, executionEnd).toMillis()
        );
    }

    /**
     * Scheduled task to reset the user cache every two hours.
     */
    @Scheduled(cron = "0 0 */2 * * *")
    public void resetCache() {
        Instant executionStart = Instant.now();
        int cachedUsers = userRepository.initializeCache();
        Instant executionEnd = Instant.now();
        log.info(
                "Reset Cache with [{} users]. Execution took [{} ms]",
                cachedUsers,
                Duration.between(executionStart, executionEnd).toMillis()
        );
    }
}
