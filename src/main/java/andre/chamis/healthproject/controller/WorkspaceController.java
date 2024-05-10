package andre.chamis.healthproject.controller;

import andre.chamis.healthproject.domain.auth.annotation.JwtAuthenticated;
import andre.chamis.healthproject.domain.patient.dto.CreatePatientDTO;
import andre.chamis.healthproject.domain.request.PaginationInfo;
import andre.chamis.healthproject.domain.response.PaginatedResponse;
import andre.chamis.healthproject.domain.response.ResponseMessage;
import andre.chamis.healthproject.domain.response.ResponseMessageBuilder;
import andre.chamis.healthproject.domain.user.dto.GetUsernameAndIdDTO;
import andre.chamis.healthproject.domain.workspace.attendance.dto.ClockInDTO;
import andre.chamis.healthproject.domain.workspace.attendance.dto.GetAttendanceDTO;
import andre.chamis.healthproject.domain.workspace.attendance.dto.GetAttendanceWithUsernameDTO;
import andre.chamis.healthproject.domain.workspace.dto.GetWorkspaceDTO;
import andre.chamis.healthproject.domain.workspace.member.dto.CreateWorkspaceMemberDTO;
import andre.chamis.healthproject.domain.workspace.member.dto.GetWorkspaceMemberDTO;
import andre.chamis.healthproject.service.WorkspaceAttendanceService;
import andre.chamis.healthproject.service.WorkspaceMemberService;
import andre.chamis.healthproject.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@JwtAuthenticated
@RequiredArgsConstructor
@RequestMapping("workspace")
public class WorkspaceController {
    private final WorkspaceService workspaceService;
    private final WorkspaceMemberService workspaceMemberService;
    private final WorkspaceAttendanceService workspaceAttendanceService;

    /**
     * Creates a new workspace.
     *
     * @param createPatientDTO The DTO containing workspace creation information.
     * @return A ResponseEntity containing a ResponseMessage with the created workspace information on success.
     */
    @PostMapping("create")
    public ResponseEntity<ResponseMessage<GetWorkspaceDTO>> createWorkspace(@RequestBody CreatePatientDTO createPatientDTO) {
        GetWorkspaceDTO body = workspaceService.createWorkspace(createPatientDTO);
        return ResponseMessageBuilder.build(body, HttpStatus.CREATED);
    }

    /**
     * Clocks in a user to a workspace.
     *
     * @param clockInDTO The DTO containing clock-in information.
     * @return A ResponseEntity containing a ResponseMessage with the attendance information on success.
     */
    @PostMapping("/clock-in")
    public ResponseEntity<ResponseMessage<GetAttendanceDTO>> clockIn(@RequestBody ClockInDTO clockInDTO) {
        GetAttendanceDTO body = workspaceAttendanceService.handleClockIn(clockInDTO);
        return ResponseMessageBuilder.build(body, HttpStatus.OK);
    }

    /**
     * Clocks out a user from a workspace.
     *
     * @return A ResponseEntity containing a ResponseMessage with the attendance information on success.
     */
    @PostMapping("/clock-out")
    public ResponseEntity<ResponseMessage<List<GetAttendanceDTO>>> clockOut() {
        List<GetAttendanceDTO> body = workspaceAttendanceService.handleClockOut();
        return ResponseMessageBuilder.build(body, HttpStatus.OK);
    }

    /**
     * Gets a paginated list of the attendances of a workspace. If username is provided, gets only for the users with that username.
     *
     * @return A ResponseEntity containing a ResponseMessage with the attendance information on success.
     */
    @GetMapping("{workspaceId}/attendances")
    public ResponseEntity<ResponseMessage<PaginatedResponse<GetAttendanceWithUsernameDTO>>> getAttendances(
            @PathVariable Long workspaceId,
            @RequestParam(required = false) Optional<Long> userId,
            PaginationInfo paginationInfo
    ) {
        PaginatedResponse<GetAttendanceWithUsernameDTO> body = workspaceAttendanceService.getAttendances(workspaceId, userId, paginationInfo);
        return ResponseMessageBuilder.build(body, HttpStatus.OK);
    }


    /**
     * Deletes a workspace.
     *
     * @param workspaceId The ID of the workspace to delete.
     * @return A ResponseEntity containing a ResponseMessage indicating success on deletion.
     */
    @DeleteMapping("{workspaceId}/delete")
    public ResponseEntity<ResponseMessage<Void>> deleteWorkspace(@PathVariable Long workspaceId) {
        workspaceService.deleteWorkspace(workspaceId);
        return ResponseMessageBuilder.build(HttpStatus.OK);
    }

    /**
     * Deactivates a workspace.
     *
     * @param workspaceId The ID of the workspace to deactivate.
     * @return A ResponseEntity containing a ResponseMessage with the deactivated workspace information on success.
     */
    @PatchMapping("{workspaceId}/deactivate")
    public ResponseEntity<ResponseMessage<GetWorkspaceDTO>> deactivateWorkspace(@PathVariable Long workspaceId) {
        workspaceService.deactivateWorkspace(workspaceId);
        return ResponseMessageBuilder.build(HttpStatus.OK);
    }

    /**
     * Activates a workspace.
     *
     * @param workspaceId The ID of the workspace to activate.
     * @return A ResponseEntity containing a ResponseMessage with the activated workspace information on success.
     */
    @PatchMapping("{workspaceId}/activate")
    public ResponseEntity<ResponseMessage<Void>> activateWorkspace(@PathVariable Long workspaceId) {
        workspaceService.activateWorkspace(workspaceId);
        return ResponseMessageBuilder.build(HttpStatus.OK);
    }

    /**
     * Retrieves members of a workspace.
     *
     * @param workspaceId    The ID of the workspace.
     * @param paginationInfo Information for pagination.
     * @return A ResponseEntity containing a ResponseMessage with the workspace members on success.
     */
    @GetMapping("{workspaceId}/members")
    public ResponseEntity<ResponseMessage<PaginatedResponse<GetWorkspaceMemberDTO>>> getMembers(
            @PathVariable Long workspaceId, PaginationInfo paginationInfo

    ) {
        PaginatedResponse<GetWorkspaceMemberDTO> body = workspaceMemberService.getAllMembersOfWorkspace(workspaceId, paginationInfo);
        return ResponseMessageBuilder.build(body, HttpStatus.OK);
    }

    /**
     * Retrieves the names of all members of a workspace.
     *
     * @param workspaceId The ID of the workspace.
     * @return A ResponseEntity containing a ResponseMessage with the names of the workspace members on success.
     */
    @GetMapping("{workspaceId}/members/names")
    public ResponseEntity<ResponseMessage<List<GetUsernameAndIdDTO>>> getMemberNames(
            @PathVariable Long workspaceId

    ) {
        List<GetUsernameAndIdDTO> body = workspaceMemberService.getAllMemberNamesOfWorkspace(workspaceId);
        return ResponseMessageBuilder.build(body, HttpStatus.OK);
    }

    /**
     * Adds a member to a workspace.
     *
     * @param workspaceId              The ID of the workspace.
     * @param createWorkspaceMemberDTO The DTO containing information to add a member.
     * @return A ResponseEntity containing a ResponseMessage with the added workspace member on success.
     */
    @PostMapping("{workspaceId}/members")
    public ResponseEntity<ResponseMessage<GetWorkspaceMemberDTO>> addMember(@PathVariable Long workspaceId, @RequestBody CreateWorkspaceMemberDTO createWorkspaceMemberDTO) {
        GetWorkspaceMemberDTO body = workspaceMemberService.addUserToWorkspace(workspaceId, createWorkspaceMemberDTO);
        return ResponseMessageBuilder.build(body, HttpStatus.OK);
    }


    @PatchMapping("{workspaceId}/members/activate")
    public ResponseEntity<ResponseMessage<Void>> activateMember(@PathVariable Long workspaceId, @RequestParam Long userId) {
        workspaceMemberService.activateWorkspaceMember(workspaceId, userId);
        return ResponseMessageBuilder.build(HttpStatus.OK);
    }

    @PatchMapping("{workspaceId}/members/deactivate")
    public ResponseEntity<ResponseMessage<Void>> deactivateMember(@PathVariable Long workspaceId, @RequestParam Long userId) {
        workspaceMemberService.deactivateWorkspaceMember(workspaceId, userId);
        return ResponseMessageBuilder.build(HttpStatus.OK);
    }
}