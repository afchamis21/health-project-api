package andre.chamis.healthproject.domain.workspace.attendance.repository;

import andre.chamis.healthproject.domain.workspace.attendance.model.WorkspaceAttendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface WorkspaceAttendanceJpaRepository extends JpaRepository<WorkspaceAttendance, Long> {
    List<WorkspaceAttendance> findAllByWorkspaceIdAndUserIdAndClockInTimeNotNullAndClockOutTimeNull(Long workspaceId, Long userId);
}
