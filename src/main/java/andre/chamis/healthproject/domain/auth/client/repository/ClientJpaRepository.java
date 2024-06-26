package andre.chamis.healthproject.domain.auth.client.repository;

import andre.chamis.healthproject.domain.auth.client.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing client entities in the database.
 * Extends {@link JpaRepository} for CRUD operations on Client entities with Long as the ID type.
 */
@Repository
interface ClientJpaRepository extends JpaRepository<Client, Long> {

    /**
     * Retrieves a client by its public key.
     *
     * @param key The public key of the client to find.
     * @return An Optional containing the client if found, otherwise empty.
     */
    Optional<Client> findClientByPublicKey(String key);
}
