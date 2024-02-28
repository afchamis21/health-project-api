package andre.chamis.healthproject.domain.workspace.attendance.repository;

import andre.chamis.healthproject.domain.workspace.attendance.model.WorkspaceAttendance;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class WorkspaceAttendanceRepository {
    private final WorkspaceAttendanceJpaRepository jpaRepository;

    public WorkspaceAttendance save(WorkspaceAttendance attendance) {
        return jpaRepository.save(attendance);
    }

    public List<WorkspaceAttendance> save(List<WorkspaceAttendance> attendances) {
        return jpaRepository.saveAll(attendances);
    }

    public List<WorkspaceAttendance> findAllClockedIn(Long currentUserId) {
        return jpaRepository.findAllByUserIdAndClockInTimeNotNullAndClockOutTimeNull(currentUserId);
    }
}
