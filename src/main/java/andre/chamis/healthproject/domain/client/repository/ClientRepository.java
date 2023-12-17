package andre.chamis.healthproject.domain.client.repository;

import andre.chamis.healthproject.domain.client.model.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ClientRepository {
    private final ClientJpaRepository clientJpaRepository;


    public Optional<Client> findClientByName(String name) {
        return clientJpaRepository.findClientByClientName(name);
    }
}
