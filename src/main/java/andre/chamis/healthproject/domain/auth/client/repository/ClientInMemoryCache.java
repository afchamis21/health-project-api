package andre.chamis.healthproject.domain.auth.client.repository;

import andre.chamis.healthproject.cache.InMemoryCache;
import andre.chamis.healthproject.domain.auth.client.model.Client;
import org.springframework.stereotype.Repository;


/**
 * Represents an in-memory cache for clients.
 * Extends {@link InMemoryCache}.
 */
@Repository
class ClientInMemoryCache extends InMemoryCache<String, Client> {
    public ClientInMemoryCache() {
        super(Client::getPublicKey);
    }
}
