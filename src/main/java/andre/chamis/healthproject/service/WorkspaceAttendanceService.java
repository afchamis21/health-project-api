package andre.chamis.healthproject.service;

import andre.chamis.healthproject.context.ServiceContext;
import andre.chamis.healthproject.domain.exception.ForbiddenException;
import andre.chamis.healthproject.domain.response.ErrorMessage;
import andre.chamis.healthproject.domain.workspace.attendance.dto.ClockInDTO;
import andre.chamis.healthproject.domain.workspace.attendance.dto.GetAttendanceDTO;
import andre.chamis.healthproject.domain.workspace.attendance.model.WorkspaceAttendance;
import andre.chamis.healthproject.domain.workspace.attendance.repository.WorkspaceAttendanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkspaceAttendanceService {
    private final UserService userService;
    private final WorkspaceMemberService workspaceMemberService;
    private final WorkspaceAttendanceRepository workspaceAttendanceRepository;

    public GetAttendanceDTO handleClockIn(ClockInDTO clockInDTO) {
        Long currentUserId = ServiceContext.getContext().getUserId();
        Long workspaceId = clockInDTO.workspaceId();

        log.info("Clocking in user [{}]", workspaceId);

        checkWorkspaceMembership(workspaceId, currentUserId);

        if (userService.checkIsClockedIn(currentUserId)) {
            clockOut(currentUserId);
        }

        return clockIn(workspaceId, currentUserId);
    }

    public List<GetAttendanceDTO> handleClockOut() {
        Long currentUserId = ServiceContext.getContext().getUserId();

        log.info("Clocking out user [{}]", currentUserId);

        return clockOut(currentUserId);
    }

    private GetAttendanceDTO clockIn(Long workspaceId, Long currentUserId) {
        WorkspaceAttendance attendance = new WorkspaceAttendance(workspaceId, currentUserId);
        attendance = workspaceAttendanceRepository.save(attendance);

        userService.clockIn(currentUserId);

        log.info("Clocked in user [{}] on workspace [{}]", currentUserId, workspaceId);

        return GetAttendanceDTO.fromAttendace(attendance);
    }

    private List<GetAttendanceDTO> clockOut(Long currentUserId) {
        List<WorkspaceAttendance> attendances = workspaceAttendanceRepository.findAllClockedIn(currentUserId);

        attendances.forEach(attendance -> attendance.setClockOutTime(Date.from(Instant.now())));

        attendances = workspaceAttendanceRepository.save(attendances);

        userService.clockOut(currentUserId);

        log.info("Clocked out user [{}]", currentUserId);

        return attendances.stream().map(GetAttendanceDTO::fromAttendace).toList();
    }

    private void checkWorkspaceMembership(Long workspaceId, Long userId) {
        if (!workspaceMemberService.isMemberOfWorkspace(workspaceId, userId)) {
            throw new ForbiddenException(ErrorMessage.INVALID_WORKSPACE_ACCESS);
        }
    }
}
