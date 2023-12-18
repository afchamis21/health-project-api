package andre.chamis.healthproject.service;

import andre.chamis.healthproject.domain.client.dto.ClientAuthDTO;
import andre.chamis.healthproject.domain.client.model.Client;
import andre.chamis.healthproject.domain.client.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public Optional<Client> validateClientCredentials(ClientAuthDTO clientAuthDTO) {
        Optional<Client> result = clientRepository.findClientByName(clientAuthDTO.clientName());

        if (result.isEmpty()) {
            return Optional.empty();
        }

        Client client = result.get();

        boolean isPasswordCorrect = bCryptPasswordEncoder.matches(clientAuthDTO.publicKey(), client.getPublicKey());

        return isPasswordCorrect ? Optional.of(client) : Optional.empty();
    }
}
