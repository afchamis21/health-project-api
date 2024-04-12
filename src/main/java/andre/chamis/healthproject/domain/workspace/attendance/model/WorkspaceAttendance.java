package andre.chamis.healthproject.domain.workspace.attendance.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents attendance information for a user in a workspace.
 */
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
    private LocalDateTime clockInTime;

    @Column(name = "clock_out_time")
    private LocalDateTime clockOutTime;

    /**
     * Constructs a new WorkspaceAttendance object with the specified workspace ID and user ID,
     * setting the clock-in time to the current time.
     *
     * @param workspaceId The ID of the workspace.
     * @param userId      The ID of the user.
     */
    public WorkspaceAttendance(Long workspaceId, Long userId) {
        this.workspaceId = workspaceId;
        this.userId = userId;
        this.clockInTime = LocalDateTime.now();
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


