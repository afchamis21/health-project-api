package andre.chamis.healthproject.domain.client.repository;

import andre.chamis.healthproject.cache.InMemoryCache;
import andre.chamis.healthproject.domain.client.model.Client;
import org.springframework.stereotype.Repository;

@Repository
public class ClientInMemoryCache extends InMemoryCache<String, Client> {
    public ClientInMemoryCache() {
        super(Client::getPublicKey);
    }
}