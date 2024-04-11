package andre.chamis.healthproject.domain.workspace.attendance.repository;

import andre.chamis.healthproject.domain.request.PaginationInfo;
import andre.chamis.healthproject.domain.response.PaginatedResponse;
import andre.chamis.healthproject.domain.workspace.attendance.dto.GetAttendanceWithUsernameDTO;
import andre.chamis.healthproject.domain.workspace.attendance.model.WorkspaceAttendance;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class WorkspaceAttendanceRepository {
    private final WorkspaceAttendanceJpaRepository jpaRepository;
    private final WorkspaceAttendanceDAO attendanceDAO;


    /**
     * Saves a workspace attendance record.
     *
     * @param attendance The attendance record to save.
     * @return The saved attendance record.
     */
    public WorkspaceAttendance save(WorkspaceAttendance attendance) {
        return jpaRepository.save(attendance);
    }

    /**
     * Saves a list of workspace attendance records.
     *
     * @param attendances The list of attendance records to save.
     * @return The saved list of attendance records.
     */
    public List<WorkspaceAttendance> save(List<WorkspaceAttendance> attendances) {
        return jpaRepository.saveAll(attendances);
    }

    /**
     * Retrieves a list of workspace attendance records for a user who is currently clocked in.
     *
     * @param currentUserId The ID of the user.
     * @return A list of workspace attendance records.
     */
    public List<WorkspaceAttendance> findAllClockedIn(Long currentUserId) {
        return jpaRepository.findAllByUserIdAndClockInTimeNotNullAndClockOutTimeNull(currentUserId);
    }

    public PaginatedResponse<GetAttendanceWithUsernameDTO> findAllByWorkspaceId(Long workspaceId, PaginationInfo paginationInfo) {
        return attendanceDAO.searchAllByWorkspaceId(workspaceId, paginationInfo);
    }

    public PaginatedResponse<GetAttendanceWithUsernameDTO> findAllByWorkspaceIdAndUserId(Long workspaceId, Long userId, PaginationInfo paginationInfo) {
        return attendanceDAO.searchAllByWorkspaceIdAndUsername(workspaceId, userId, paginationInfo);
    }
}
