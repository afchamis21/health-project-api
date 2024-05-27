package andre.chamis.healthproject.domain.attendance.dto;

import java.util.Date;

public record GetAttendanceWithUsernameDTO(Long patientId, Long userId, Date clockInTime, Date clockOutTime,
                                           String username) {
}
