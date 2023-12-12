package andre.chamis.healthproject.domain.auth.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "otps")
public class OTP {
    @Id
    @GeneratedValue
    @Column(name = "otp_id")
    private Long otpId;

    private String code;

    @Column(name = "user_id")
    private Long userId;
}
