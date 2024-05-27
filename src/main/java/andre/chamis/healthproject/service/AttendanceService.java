package andre.chamis.healthproject.service;

import andre.chamis.healthproject.domain.attendance.dto.GetAttendanceDTO;
import andre.chamis.healthproject.domain.attendance.dto.GetAttendanceWithUsernameDTO;
import andre.chamis.healthproject.domain.attendance.model.Attendance;
import andre.chamis.healthproject.domain.attendance.repository.AttendanceRepository;
import andre.chamis.healthproject.domain.request.PaginationInfo;
import andre.chamis.healthproject.domain.response.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;


    protected GetAttendanceDTO clockIn(Long patientId, Long currentUserId) {
        Attendance attendance = new Attendance(patientId, currentUserId);
        attendance = attendanceRepository.save(attendance);

        log.info("Clocked in user [{}] on patient [{}]", currentUserId, patientId);

        return GetAttendanceDTO.fromAttendance(attendance);
    }

    protected List<GetAttendanceDTO> clockOut(Long currentUserId) {
        log.info("Clocking out user [{}]", currentUserId);

        List<Attendance> attendances = attendanceRepository.findAllClockedIn(currentUserId);

        attendances.forEach(attendance -> attendance.setClockOutTime(LocalDateTime.now()));

        attendances = attendanceRepository.save(attendances);

        log.info("Clocked out user [{}]", currentUserId);

        return attendances.stream().map(GetAttendanceDTO::fromAttendance).toList();
    }

    protected PaginatedResponse<GetAttendanceWithUsernameDTO> getAttendances(Long patientId, Optional<Long> userId, PaginationInfo paginationInfo) {
        if (userId.isEmpty()) {
            return attendanceRepository.findAllByPatientId(patientId, paginationInfo);
        }

        return attendanceRepository.findAllByPatientIdAndUserId(patientId, userId.get(), paginationInfo);
    }
}
