package andre.chamis.healthproject.service;

import andre.chamis.healthproject.context.ServiceContext;
import andre.chamis.healthproject.domain.exception.ForbiddenException;
import andre.chamis.healthproject.domain.response.ErrorMessage;
import andre.chamis.healthproject.domain.workspace.attendance.dto.ClockInDTO;
import andre.chamis.healthproject.domain.workspace.attendance.dto.ClockOutDTO;
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
    private final WorkspaceMemberService workspaceMemberService;
    private final WorkspaceAttendanceRepository workspaceAttendanceRepository;

    public GetAttendanceDTO clockIn(ClockInDTO clockInDTO) {
        Long currentUserId = ServiceContext.getContext().getUserId();
        Long workspaceId = clockInDTO.workspaceId();

        log.info("Clocking in user [{}]", workspaceId);

        if (!workspaceMemberService.isMemberOfWorkspace(workspaceId, currentUserId)) {
            throw new ForbiddenException(ErrorMessage.INVALID_WORKSPACE_ACCESS);
        }

        WorkspaceAttendance attendance = new WorkspaceAttendance(workspaceId, currentUserId);
        attendance = workspaceAttendanceRepository.save(attendance);

        workspaceMemberService.clockIn(workspaceId, currentUserId);

        log.info("Clocked in user [{}] on workspace [{}]", currentUserId, workspaceId);

        return GetAttendanceDTO.fromAttendace(attendance);
    }

    public List<GetAttendanceDTO> clockOut(ClockOutDTO clockOutDTO) {
        Long currentUserId = ServiceContext.getContext().getUserId();
        Long workspaceId = clockOutDTO.workspaceId();

        log.info("Clocking out user [{}]", currentUserId);

        if (!workspaceMemberService.isMemberOfWorkspace(workspaceId, currentUserId)) {
            throw new ForbiddenException(ErrorMessage.INVALID_WORKSPACE_ACCESS);
        }

        List<WorkspaceAttendance> attendances = workspaceAttendanceRepository.findAllClockedIn(workspaceId, currentUserId);

        attendances.forEach(attendance -> attendance.setClockOutTime(Date.from(Instant.now())));

        attendances = workspaceAttendanceRepository.save(attendances);

        workspaceMemberService.clockOut(workspaceId, currentUserId);

        log.info("Clocked out user [{}] from workspace [{}]", currentUserId, workspaceId);

        return attendances.stream().map(GetAttendanceDTO::fromAttendace).toList();
    }
}
