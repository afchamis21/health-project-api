package andre.chamis.healthproject.service;

import andre.chamis.healthproject.domain.client.model.Client;
import andre.chamis.healthproject.domain.client.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;

    public Optional<Client> findClientByKey(String apiKey) {
        return clientRepository.findClientByKey(apiKey);
    }
}
