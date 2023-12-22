package andre.chamis.healthproject.domain.client.repository;

import andre.chamis.healthproject.domain.client.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientJpaRepository extends JpaRepository<Client, Long> {
    Optional<Client> findClientByPublicKey(String key);
}
