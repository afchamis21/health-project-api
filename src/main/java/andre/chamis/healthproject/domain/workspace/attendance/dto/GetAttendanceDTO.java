package andre.chamis.healthproject.domain.workspace.attendance.dto;

import andre.chamis.healthproject.domain.workspace.attendance.model.WorkspaceAttendance;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public final class GetAttendanceDTO {
    private final Long workspaceId;
    private final Long userId;
    private final Date clockInTime;
    private final Date clockOutTime;

    public static GetAttendanceDTO fromAttendace(WorkspaceAttendance attendance) {
        return new GetAttendanceDTO(
                attendance.getWorkspaceId(),
                attendance.getUserId(),
                attendance.getClockInTime(),
                attendance.getClockOutTime()
        );
    }
}
