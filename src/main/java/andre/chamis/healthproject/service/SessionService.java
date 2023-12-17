package andre.chamis.healthproject.service;


import andre.chamis.healthproject.context.ServiceContext;
import andre.chamis.healthproject.domain.exception.ForbiddenException;
import andre.chamis.healthproject.domain.session.model.Session;
import andre.chamis.healthproject.domain.session.property.SessionProperties;
import andre.chamis.healthproject.domain.session.repository.SessionRepository;
import andre.chamis.healthproject.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

/**
 * Service class responsible for handling user sessions.
 */
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
        return sessionRepository.save(session);
    }

    /**
     * Deletes all expired sessions.
     *
     * @return The number of deleted expired sessions.
     */
    public int deleteAllExpired() {
        return sessionRepository.deleteAllExpired();
    }

    /**
     * Retrieves a session by its ID.
     *
     * @param sessionId The ID of the session to retrieve.
     * @return An optional containing the retrieved session, if found.
     */
    public Optional<Session> findSessionById(Long sessionId) {
        return sessionRepository.findById(sessionId);
    }

    /**
     * Retrieves the user ID associated with the current session.
     *
     * @return The ID of the user associated with the current session.
     */
    public Long getCurrentUserId() {
        Long sessionId = ServiceContext.getContext().getSessionId();
        Optional<Session> sessionOptional = findSessionById(sessionId);
        Session session = sessionOptional.orElseThrow(ForbiddenException::new);
        return session.getUserId();
    }


    /**
     * Validates that a session is not expired.
     *
     * @param session The session to validate.
     * @return True if the session is not expired, otherwise false.
     */
    public boolean validateSessionIsNotExpired(Session session) {
        return session.getExpireDt().after(Date.from(Instant.now()));
    }

    /**
     * Deletes the current session.
     */
    public void deleteCurrentSession() {
        deleteSessionById(ServiceContext.getContext().getSessionId());
    }

    /**
     * Deletes a session by its ID.
     *
     * @param sessionId The ID of the session to delete.
     */
    public void deleteSessionById(Long sessionId) {
        sessionRepository.deleteSessionById(sessionId);
    }
}
