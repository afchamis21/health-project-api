package andre.chamis.healthproject.domain.health.attendance.dto;


import andre.chamis.healthproject.domain.health.attendance.model.Attendance;

import java.time.LocalDateTime;

/**
 * Data transfer object (DTO) representing attendance information.
 *
 * @param patientId    The ID of the patient.
 * @param userId       The ID of the user.
 * @param clockInTime  The timestamp indicating the time of clock-in.
 * @param clockOutTime The timestamp indicating the time of clock-out.
 */
public record GetAttendanceDTO(Long patientId, Long userId, LocalDateTime clockInTime, LocalDateTime clockOutTime) {

    /**
     * Creates a GetAttendanceDTO object from an Attendance entity.
     *
     * @param attendance The Attendance entity to convert.
     * @return The corresponding GetAttendanceDTO object.
     */
    public static GetAttendanceDTO fromAttendance(Attendance attendance) {
        return new GetAttendanceDTO(
                attendance.getPatientId(),
                attendance.getUserId(),
                attendance.getClockInTime(),
                attendance.getClockOutTime()
        );
    }
}

