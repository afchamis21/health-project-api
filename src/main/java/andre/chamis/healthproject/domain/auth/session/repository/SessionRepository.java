package andre.chamis.healthproject.domain.auth.session.repository;

import andre.chamis.healthproject.exception.ForbiddenException;
import andre.chamis.healthproject.infra.request.response.ErrorMessage;
import andre.chamis.healthproject.domain.auth.session.model.Session;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Repository class for managing session entities using both in-memory caching and JPA.
 */
@Repository
@RequiredArgsConstructor
public class SessionRepository {
    private final SessionInMemoryCache inMemoryCache;
    private final SessionJpaRepository jpaRepository;

    /**
     * Initializes the in-memory cache with data from the database.
     */
    @PostConstruct
    void initializeCache() {
        inMemoryCache.initializeCache(jpaRepository.findAll());
    }

    /**
     * Saves a session, updating both the database and the in-memory cache.
     *
     * @param session The session to be saved.
     * @return The saved session.
     */
    public Session save(Session session) {
        session = jpaRepository.save(session);
        inMemoryCache.put(session);

        return session;
    }

    /**
     * Finds a session by its ID, first checking the in-memory cache, then the database.
     *
     * @param sessionId The ID of the session to find.
     * @return An {@link Optional} containing the found session, or empty if not found.
     */
    public Optional<Session> findById(Long sessionId) {
        if (sessionId == null) {
            throw new ForbiddenException(ErrorMessage.NO_SESSION);
        }

        Optional<Session> sessionOptionalFromCache = inMemoryCache.get(sessionId);
        if (sessionOptionalFromCache.isPresent()) {
            return sessionOptionalFromCache;
        }

        Optional<Session> sessionOptionalFromDatabase = jpaRepository.findById(sessionId);
        sessionOptionalFromDatabase.ifPresent(inMemoryCache::put);

        return sessionOptionalFromDatabase;
    }

    /**
     * Deletes all expired sessions from both the database and the in-memory cache.
     *
     * @return The number of deleted sessions.
     */
    public int deleteAllExpired() {
        List<Session> deletedSessions = jpaRepository.deleteAllByExpireDtBefore(Date.from(Instant.now()));
        inMemoryCache.deleteFromList(deletedSessions);
        return deletedSessions.size();
    }

    /**
     * Deletes a session by its ID from both the database and the in-memory cache.
     *
     * @param sessionId The ID of the session to delete.
     */
    public void deleteSessionById(Long sessionId) {
        jpaRepository.deleteById(sessionId);
        inMemoryCache.remove(sessionId);
    }

    /**
     * Deletes all sessions associated with a given user ID from both the database and the in-memory cache.
     *
     * @param userId The ID of the user whose sessions will be deleted.
     */
    @Transactional
    public void deleteSessionsByUserId(Long userId) {
        List<Session> sessions = jpaRepository.deleteAllByUserId(userId);
        inMemoryCache.deleteFromList(sessions);
    }
}
