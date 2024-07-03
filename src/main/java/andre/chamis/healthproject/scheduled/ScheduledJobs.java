package andre.chamis.healthproject.scheduled;


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

    // TODO Criar um job para rodar todo dia 00:00 que vai buscar todos os clientes com subscription do banco
    //  (talvez uma querie custom jdbc), ou buscar todas as subscriptions e analisar num for
    //  (a querie deve ser mais rápida e é melhor pq acessar menos o banco = mais barato) e enviar um email para todos
    //  que a assinatura estiver a 15 dias de vencer e a flag cancelAtPeriodEnd = true, avisando e convidando a
    //  reassinar. A gente vai estilizar o email certinho mais pra frente, por enquanto não precisa nem do link
}
