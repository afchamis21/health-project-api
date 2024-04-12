package andre.chamis.healthproject.domain.workspace.attendance.dto;

import andre.chamis.healthproject.domain.workspace.attendance.model.WorkspaceAttendance;

import java.time.LocalDateTime;

/**
 * Data transfer object (DTO) representing attendance information.
 *
 * @param workspaceId  The ID of the workspace.
 * @param userId       The ID of the user.
 * @param clockInTime  The timestamp indicating the time of clock-in.
 * @param clockOutTime The timestamp indicating the time of clock-out.
 */
public record GetAttendanceDTO(Long workspaceId, Long userId, LocalDateTime clockInTime, LocalDateTime clockOutTime) {

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

