package andre.chamis.healthproject.domain.workspace.attendance.dto;

import java.util.Date;

public record GetAttendanceWithUsernameDTO(Long workspaceId, Long userId, Date clockInTime, Date clockOutTime,
                                           String username) {
}
