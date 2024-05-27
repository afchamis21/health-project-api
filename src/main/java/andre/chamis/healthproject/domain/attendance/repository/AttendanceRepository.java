package andre.chamis.healthproject.domain.attendance.repository;

import andre.chamis.healthproject.domain.attendance.dto.GetAttendanceWithUsernameDTO;
import andre.chamis.healthproject.domain.attendance.model.Attendance;
import andre.chamis.healthproject.domain.request.PaginationInfo;
import andre.chamis.healthproject.domain.response.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AttendanceRepository {
    private final AttendanceJpaRepository jpaRepository;
    private final AttendanceDAO attendanceDAO;


    public Attendance save(Attendance attendance) {
        return jpaRepository.save(attendance);
    }

    public List<Attendance> save(List<Attendance> attendances) {
        return jpaRepository.saveAll(attendances);
    }

    public List<Attendance> findAllClockedIn(Long currentUserId) {
        return jpaRepository.findAllByUserIdAndClockInTimeNotNullAndClockOutTimeNull(currentUserId);
    }

    public PaginatedResponse<GetAttendanceWithUsernameDTO> findAllByPatientId(Long patientId, PaginationInfo paginationInfo) {
        return attendanceDAO.searchAllByPatientId(patientId, paginationInfo);
    }

    public PaginatedResponse<GetAttendanceWithUsernameDTO> findAllByPatientIdAndUserId(Long patientId, Long userId, PaginationInfo paginationInfo) {
        return attendanceDAO.searchAllBysearchAllByPatientIdAndUsername(patientId, userId, paginationInfo);
    }
}
