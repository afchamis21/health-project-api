package andre.chamis.healthproject.domain.collaborator.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Date;


@Data
@Entity
@NoArgsConstructor
@Table(name = "collaborator")
public class Collaborator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "collaborator_id")
    private Long collaboratorId;

    @Column(name = "patient_id")
    private Long patientId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "create_dt")
    private Date createDt;

    @Column(name = "is_active")
    private boolean active;

    public Collaborator(Long patientId, Long userId) {
        this.patientId = patientId;
        this.userId = userId;
        this.active = true;
        this.createDt = Date.from(Instant.now());
    }

    @Override
    public String toString() {
        return "Collaborator{" +
                "collaboratorId=" + collaboratorId +
                ", patientId=" + patientId +
                ", userId=" + userId +
                ", createDt=" + createDt +
                ", active=" + active +
                '}';
    }
}
