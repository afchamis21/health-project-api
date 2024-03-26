package andre.chamis.healthproject.domain.workspace.attendance.dto;

import andre.chamis.healthproject.domain.workspace.attendance.model.WorkspaceAttendance;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * Data transfer object (DTO) representing attendance information.
 */
@Data
@AllArgsConstructor
public final class GetAttendanceDTO {
    /**
     * The ID of the workspace.
     */
    private final Long workspaceId;

    /**
     * The ID of the user.
     */
    private final Long userId;

    /**
     * The timestamp indicating the time of clock-in.
     */
    private final Date clockInTime;

    /**
     * The timestamp indicating the time of clock-out.
     */
    private final Date clockOutTime;

    /**
     * Creates a GetAttendanceDTO object from a WorkspaceAttendance entity.
     *
     * @param attendance The WorkspaceAttendance entity to convert.
     * @return The corresponding GetAttendanceDTO object.
     */
    public static GetAttendanceDTO fromAttendance(WorkspaceAttendance attendance) {
        return new GetAttendanceDTO(
                attendance.getWorkspaceId(),
                attendance.getUserId(),
                attendance.getClockInTime(),
                attendance.getClockOutTime()
        );
    }
}

