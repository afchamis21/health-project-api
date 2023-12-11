package andre.chamis.healthproject.domain.user.repository;

import andre.chamis.healthproject.cache.InMemoryCache;
import andre.chamis.healthproject.domain.user.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * An {@link InMemoryCache} for caching user entities in memory.
 */
@Repository
public class UserInMemoryCache extends InMemoryCache<Long, User> {

    public UserInMemoryCache() {
        super(User::getUserId);
    }

    /**
     * Finds and returns a set of users with usernames that match the given prefix.
     *
     * @param username The username prefix to match against.
     * @return A set of users with matching usernames.
     */
    public List<User> findAllWithMatchingUsername(String username){
        Map<Long, User> cache = super.getCache();

        return cache.values().stream().filter(
                (User user) -> user.getUsername().startsWith(username)
        ).collect(Collectors.toList());
    }
}
