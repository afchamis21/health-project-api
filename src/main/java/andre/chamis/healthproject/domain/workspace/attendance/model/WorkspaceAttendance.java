package andre.chamis.healthproject.domain.workspace.attendance.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@Table(name = "workspace_attendance")
public class WorkspaceAttendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "workspace_id")
    private Long workspaceId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "clock_in_time")
    private Date clockInTime;

    @Column(name = "clock_out_time")
    private Date clockOutTime;

    public WorkspaceAttendance(Long workspaceId, Long userId) {
        this.workspaceId = workspaceId;
        this.userId = userId;
        this.clockInTime = Date.from(Instant.now());
    }

    @Override
    public String toString() {
        return "WorkspaceAttendance{" +
                "id=" + id +
                ", workspaceId=" + workspaceId +
                ", userId=" + userId +
                ", clockInTime=" + clockInTime +
                ", clockOutTime=" + clockOutTime +
                '}';
    }
}

