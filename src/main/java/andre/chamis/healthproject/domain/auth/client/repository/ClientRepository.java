package andre.chamis.healthproject.domain.auth.client.repository;

import andre.chamis.healthproject.domain.auth.client.model.Client;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ClientRepository {
    private final ClientJpaRepository clientJpaRepository;
    private final ClientInMemoryCache clientInMemoryCache;

    /**
     * Initializes the in-memory cache with data from the database.
     *
     * @return The number of clients loaded into the cache.
     */
    @PostConstruct
    public int initializeCache() {
        List<Client> clients = clientJpaRepository.findAll();
        clientInMemoryCache.initializeCache(clients);
        return clientInMemoryCache.getSize();
    }

    /**
     * Finds a client by its API key, first checking the in-memory cache and then the database if not found in cache.
     *
     * @param apiKey The API key of the client to be found.
     * @return An @{@link  Optional} containing the found client or empty if not found.
     */
    public Optional<Client> findClientByKey(String apiKey) {
        Optional<Client> resultFromCache = clientInMemoryCache.get(apiKey);
        if (resultFromCache.isPresent()) {
            return resultFromCache;
        }

        Optional<Client> resultFromDatabase = clientJpaRepository.findClientByPublicKey(apiKey);

        resultFromDatabase.ifPresent(clientInMemoryCache::put);

        return resultFromDatabase;
    }
}
