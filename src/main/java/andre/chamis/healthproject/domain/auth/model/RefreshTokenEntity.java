package andre.chamis.healthproject.domain.auth.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

/**
 * Represents a refresh token entity for token-based authentication.
 */
@Data
@Entity
@Table(name = "refresh_tokens")
public class RefreshTokenEntity {
    /**
     * Represents a refresh token entity for token-based authentication.
     */
    @Id
    @GeneratedValue
    @Column(name = "refresh_token_id")
    private Long refreshTokenId;

    /**
     * Represents a refresh token entity for token-based authentication.
     */
    private String username;

    /**
     * The token string representing the refresh token.
     */
    private String token;

    /**
     * The token string representing the refresh token.
     */
    @Column(name = "create_dt")
    private Date createDt;

    /**
     * The date and time when the refresh token will expire.
     */
    @Column(name = "expire_dt")
    private Date expireDt;
}
