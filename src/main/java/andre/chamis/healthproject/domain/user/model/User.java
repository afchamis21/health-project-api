package andre.chamis.healthproject.domain.user.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

/**
 * Represents a user in the system.
 */
@Data
@Entity
@Table(name = "users")
public class User {
    /**
     * The unique identifier of the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Long userId;

    /**
     * The Stripe client ID associated with the user.
     */
    @Column(name = "stripe_client_id")
    private String stripeClientId;

    /**
     * The unique username of the user.
     */
    private String username;

    /**
     * The email address of the user.
     */
    @Column(unique = true)
    private String email;

    /**
     * The password of the user.
     */
    private String password;

    /**
     * Indicates whether the user registration is complete or not.
     */
    @Column(name = "is_registration_complete")
    private boolean isRegistrationComplete;

    /**
     * Indicates whether the user is active or not.
     */
    @Column(name = "is_active")
    private boolean isActive;

    /**
     * Indicates whether the user has paid or not.
     */
    @Column(name = "is_payment_active")
    private boolean isPaymentActive;


    /**
     * The creation date of the user's record.
     */
    @Column(name = "create_dt")
    private Date createDt;

    /**
     * The update date of the user's record.
     */
    @Column(name = "update_dt")
    private Date updateDt;

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", stripeClientId='" + stripeClientId + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", isRegistrationComplete=" + isRegistrationComplete +
                ", isActive=" + isActive +
                ", isPaymentActive=" + isPaymentActive +
                ", createDt=" + createDt +
                ", updateDt=" + updateDt +
                '}';
    }
}
