package andre.chamis.healthproject.service;

import andre.chamis.healthproject.domain.auth.client.model.Client;
import andre.chamis.healthproject.domain.auth.client.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class for client-related operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;

    /**
     * Finds a client by its API key.
     *
     * @param apiKey The API key to search for.
     * @return An {@link Optional} containing the found client, or empty if not found.
     */
    public Optional<Client> findClientByKey(String apiKey) {
        return clientRepository.findClientByKey(apiKey);
    }
}
