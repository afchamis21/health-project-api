package andre.chamis.healthproject.domain.workspace.attendance.repository;

import andre.chamis.healthproject.domain.workspace.attendance.model.WorkspaceAttendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for managing workspace attendance entities.
 */
interface WorkspaceAttendanceJpaRepository extends JpaRepository<WorkspaceAttendance, Long> {

    /**
     * Retrieves a list of workspace attendance records for a user with a non-null clock-in time and a null clock-out time.
     *
     * @param userId The ID of the user.
     * @return A list of workspace attendance records.
     */
    List<WorkspaceAttendance> findAllByUserIdAndClockInTimeNotNullAndClockOutTimeNull(Long userId);
}
