package andre.chamis.healthproject.domain.client.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "clients")
public class Client {
    @Id
    private Long id;

    @Column(name = "client_name")
    private String clientName;

    @Column(name = "public_key")
    private String publicKey;

    private boolean active;

    @Column(name = "create_dt")
    private Date createDt;
}
