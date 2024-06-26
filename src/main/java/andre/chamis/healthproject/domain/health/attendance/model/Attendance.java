package andre.chamis.healthproject.domain.health.attendance.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents attendance information for a user.
 */
@Data
@Entity
@NoArgsConstructor
@Table(name = "attendance")
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patient_id")
    private Long patientId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "clock_in_time")
    private LocalDateTime clockInTime;

    @Column(name = "clock_out_time")
    private LocalDateTime clockOutTime;

    public Attendance(Long patientId, Long userId) {
        this.patientId = patientId;
        this.userId = userId;
        this.clockInTime = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Attendance{" +
                "id=" + id +
                ", patientId=" + patientId +
                ", userId=" + userId +
                ", clockInTime=" + clockInTime +
                ", clockOutTime=" + clockOutTime +
                '}';
    }
}


