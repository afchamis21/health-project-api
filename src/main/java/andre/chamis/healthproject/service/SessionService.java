package andre.chamis.healthproject.service;


import andre.chamis.healthproject.context.ServiceContext;
import andre.chamis.healthproject.domain.auth.session.model.Session;
import andre.chamis.healthproject.domain.auth.session.repository.SessionRepository;
import andre.chamis.healthproject.domain.user.model.User;
import andre.chamis.healthproject.properties.SessionProperties;
import andre.chamis.healthproject.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

/**
 * Service class responsible for handling user sessions.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;
    private final SessionProperties sessionProperties;

    /**
     * Creates a new session for the given user.
     *
     * @param user The user for whom the session is being created.
     * @return The created session.
     */
    public Session createSession(User user) {
        Session session = new Session();
        session.setUserId(user.getUserId());
        session.setCreateDt(Date.from(Instant.now()));
        session.setExpireDt(Date.from(Instant.now().plus(
                sessionProperties.getDuration(), sessionProperties.getUnit()
        )));

        log.info("New session created for user [{}]. Expires at [{}]", user.getEmail(), session.getExpireDt());

        return sessionRepository.save(session);
    }

    /**
     * Deletes all expired sessions.
     *
     * @return The number of deleted expired sessions.
     */
    public int deleteAllExpired() {
        log.info("Deleting all expired sessions currently on the database!");
        return sessionRepository.deleteAllExpired();
    }

    /**
     * Retrieves a session by its ID.
     *
     * @param sessionId The ID of the session to retrieve.
     * @return An optional containing the retrieved session, if found.
     */
    public Optional<Session> findSessionById(Long sessionId) {
        log.info("Searching for session with id [{}]", sessionId);
        return sessionRepository.findById(sessionId);
    }


    /**
     * Validates that a session is not expired.
     *
     * @param session The session to validate.
     * @return True if the session is not expired, otherwise false.
     */
    public boolean validateSessionIsNotExpired(Session session) {
        log.info("Validating if session is expired. Session expired date [{}]. Current Date [{}]",
                session.getExpireDt(), Date.from(Instant.now())
        );
        return DateUtils.isDateInFuture(session.getExpireDt());
    }

    /**
     * Deletes the current session.
     */
    public void deleteCurrentSession() {
        log.info("Deleting current session. Session Id [{}]", ServiceContext.getContext().getSessionId());
        deleteSessionById(ServiceContext.getContext().getSessionId());
    }

    /**
     * Deletes a session by its ID.
     *
     * @param sessionId The ID of the session to delete.
     */
    public void deleteSessionById(Long sessionId) {
        log.info("Deleting session with id [{}]", sessionId);
        sessionRepository.deleteSessionById(sessionId);
    }

    /**
     * Deletes all sessions for the current user.
     */
    public void deleteAllUserSessions() {
        Long currentUserId = ServiceContext.getContext().getUserId();
        log.info("Deleting all sessions for current user [{}]", currentUserId);
        sessionRepository.deleteSessionsByUserId(currentUserId);
    }
}
