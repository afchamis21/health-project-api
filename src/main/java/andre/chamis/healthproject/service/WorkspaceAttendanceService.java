package andre.chamis.healthproject.service;

import andre.chamis.healthproject.context.ServiceContext;
import andre.chamis.healthproject.domain.request.PaginationInfo;
import andre.chamis.healthproject.domain.response.PaginatedResponse;
import andre.chamis.healthproject.domain.workspace.attendance.dto.ClockInDTO;
import andre.chamis.healthproject.domain.workspace.attendance.dto.GetAttendanceDTO;
import andre.chamis.healthproject.domain.workspace.attendance.dto.GetAttendanceWithUsernameDTO;
import andre.chamis.healthproject.domain.workspace.attendance.model.WorkspaceAttendance;
import andre.chamis.healthproject.domain.workspace.attendance.repository.WorkspaceAttendanceRepository;
import andre.chamis.healthproject.domain.workspace.member.model.WorkspaceMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkspaceAttendanceService {
    private final UserService userService;
    private final WorkspaceService workspaceService;
    private final WorkspaceMemberService workspaceMemberService;
    private final WorkspaceAttendanceRepository workspaceAttendanceRepository;

    /**
     * Handles the clock-in process for a user.
     *
     * @param clockInDTO The DTO containing the workspace ID for clocking in.
     * @return The attendance details after clocking in.
     */
    public GetAttendanceDTO handleClockIn(ClockInDTO clockInDTO) {
        Long currentUserId = ServiceContext.getContext().getUserId();
        Long workspaceId = clockInDTO.workspaceId();

        log.info("Clocking in user [{}]", workspaceId);

        WorkspaceMember workspaceMember = workspaceMemberService.findWorkspaceMemberIfActiveOrThrow(workspaceId, currentUserId);

        if (userService.checkIsClockedIn(workspaceMember.getUserId())) {
            clockOut(currentUserId);
        }

        return clockIn(workspaceId, currentUserId);
    }

    /**
     * Handles the clock-out process for a user.
     *
     * @return The attendance details after clocking out.
     */
    public List<GetAttendanceDTO> handleClockOut() {
        Long currentUserId = ServiceContext.getContext().getUserId();

        log.info("Clocking out user [{}]", currentUserId);

        return clockOut(currentUserId);
    }

    private GetAttendanceDTO clockIn(Long workspaceId, Long currentUserId) {
        WorkspaceAttendance attendance = new WorkspaceAttendance(workspaceId, currentUserId);
        attendance = workspaceAttendanceRepository.save(attendance);

        userService.clockIn(currentUserId, workspaceId);

        log.info("Clocked in user [{}] on workspace [{}]", currentUserId, workspaceId);

        return GetAttendanceDTO.fromAttendance(attendance);
    }

    private List<GetAttendanceDTO> clockOut(Long currentUserId) {
        List<WorkspaceAttendance> attendances = workspaceAttendanceRepository.findAllClockedIn(currentUserId);

        attendances.forEach(attendance -> attendance.setClockOutTime(LocalDateTime.now()));

        attendances = workspaceAttendanceRepository.save(attendances);

        userService.clockOut(currentUserId);

        log.info("Clocked out user [{}]", currentUserId);

        return attendances.stream().map(GetAttendanceDTO::fromAttendance).toList();
    }

    public PaginatedResponse<GetAttendanceWithUsernameDTO> getAttendances(Long workspaceId, Optional<Long> userId, PaginationInfo paginationInfo) {
        workspaceService.checkWorkspaceOwnershipOrThrow(workspaceId);

        if (userId.isEmpty()) {
            return workspaceAttendanceRepository.findAllByWorkspaceId(workspaceId, paginationInfo);
        }

        return workspaceAttendanceRepository.findAllByWorkspaceIdAndUserId(workspaceId, userId.get(), paginationInfo);
    }
}
