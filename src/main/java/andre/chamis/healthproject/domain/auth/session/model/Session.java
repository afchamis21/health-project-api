package andre.chamis.healthproject.domain.auth.session.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

/**
 * Represents a user session.
 */
@Data
@Entity
@Table(name = "sessions")
public class Session {
    /**
     * The unique identifier of the session.
     */
    @Id
    @GeneratedValue
    @Column(name = "session_id")
    private Long sessionId;

    /**
     * The ID of the user associated with the session.
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * The date and time when the session was created.
     */
    @Column(name = "create_dt")
    private Date createDt;

    /**
     * The date and time when the session will expire.
     */
    @Column(name = "expire_dt")
    private Date expireDt;
}
