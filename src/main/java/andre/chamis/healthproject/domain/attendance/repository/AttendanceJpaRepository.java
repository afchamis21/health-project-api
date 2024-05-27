package andre.chamis.healthproject.domain.attendance.repository;

import andre.chamis.healthproject.domain.attendance.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for managing attendance entities.
 */
interface AttendanceJpaRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findAllByUserIdAndClockInTimeNotNullAndClockOutTimeNull(Long userId);
}
