package andre.chamis.healthproject.domain.user.repository;

import andre.chamis.healthproject.cache.InMemoryCache;
import andre.chamis.healthproject.domain.user.model.User;
import org.springframework.stereotype.Repository;

/**
 * An {@link InMemoryCache} for caching user entities in memory.
 */
@Repository
class UserInMemoryCache extends InMemoryCache<Long, User> {

    public UserInMemoryCache() {
        super(User::getUserId);
    }
}
