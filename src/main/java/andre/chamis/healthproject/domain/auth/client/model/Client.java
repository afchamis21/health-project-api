package andre.chamis.healthproject.domain.auth.client.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.Date;

/**
 * Represents a client entity.
 */
@Data
@Entity
@Table(name = "clients")
public class Client {
    /**
     * The ID of the client.
     */
    @Id
    private Long id;

    /**
     * The name of the client.
     */
    @Column(name = "client_name")
    private String clientName;

    /**
     * The public key associated with the client.
     */
    @Column(name = "public_key")
    private String publicKey;

    /**
     * Indicates whether the client is active.
     */
    private boolean active;

    /**
     * The date and time when the client was created.
     */
    @Column(name = "create_dt")
    private Date createDt;
}
