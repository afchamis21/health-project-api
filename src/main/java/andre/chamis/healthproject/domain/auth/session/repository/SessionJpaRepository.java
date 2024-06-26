package andre.chamis.healthproject.domain.auth.session.repository;

import andre.chamis.healthproject.domain.auth.session.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


/**
 * Repository interface for managing session entities using JPA.
 */
@Repository
interface SessionJpaRepository extends JpaRepository<Session, Long> {
    /**
     * Deletes all sessions that have an expiration date before the specified date.
     *
     * @param expireDt The date to compare the session expiration dates against.
     * @return The list of deleted sessions.
     */
    List<Session> deleteAllByExpireDtBefore(Date expireDt);

    /**
     * Deletes all sessions associated with a specific user.
     *
     * @param userId The ID of the user whose sessions will be deleted.
     * @return The list of deleted sessions.
     */
    List<Session> deleteAllByUserId(Long userId);
}
