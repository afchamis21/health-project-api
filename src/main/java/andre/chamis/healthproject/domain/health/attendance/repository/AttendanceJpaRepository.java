package andre.chamis.healthproject.domain.health.attendance.repository;

import andre.chamis.healthproject.domain.health.attendance.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for managing attendance entities.
 */
interface AttendanceJpaRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findAllByUserIdAndClockInTimeNotNullAndClockOutTimeNull(Long userId);
}
