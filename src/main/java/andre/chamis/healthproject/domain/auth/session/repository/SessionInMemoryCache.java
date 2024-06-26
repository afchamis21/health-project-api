package andre.chamis.healthproject.domain.auth.session.repository;


import andre.chamis.healthproject.cache.InMemoryCache;
import andre.chamis.healthproject.domain.auth.session.model.Session;
import org.springframework.stereotype.Repository;


/**
 * Repository class for caching session entities in memory.
 */
@Repository
class SessionInMemoryCache extends InMemoryCache<Long, Session> {

    public SessionInMemoryCache() {
        super(Session::getSessionId);
    }
}
