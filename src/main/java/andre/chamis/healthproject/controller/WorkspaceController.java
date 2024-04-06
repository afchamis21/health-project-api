package andre.chamis.healthproject.controller;

import andre.chamis.healthproject.domain.auth.annotation.JwtAuthenticated;
import andre.chamis.healthproject.domain.request.PaginationInfo;
import andre.chamis.healthproject.domain.response.PaginatedResponse;
import andre.chamis.healthproject.domain.response.ResponseMessage;
import andre.chamis.healthproject.domain.response.ResponseMessageBuilder;
import andre.chamis.healthproject.domain.workspace.attendance.dto.ClockInDTO;
import andre.chamis.healthproject.domain.workspace.attendance.dto.GetAttendanceDTO;
import andre.chamis.healthproject.domain.workspace.dto.CreateWorkspaceDTO;
import andre.chamis.healthproject.domain.workspace.dto.GetWorkspaceDTO;
import andre.chamis.healthproject.domain.workspace.dto.UpdateWorkspaceDTO;
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
     * @param createWorkspaceDTO The DTO containing workspace creation information.
     * @return A ResponseEntity containing a ResponseMessage with the created workspace information on success.
     */
    @PostMapping("create")
    public ResponseEntity<ResponseMessage<GetWorkspaceDTO>> createWorkspace(@RequestBody CreateWorkspaceDTO createWorkspaceDTO) {
        GetWorkspaceDTO body = workspaceService.createWorkspace(createWorkspaceDTO);
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
     * Retrieves workspace information.
     *
     * @param workspaceId The ID of the workspace to retrieve.
     * @return A ResponseEntity containing a ResponseMessage with the retrieved workspace information on success.
     */
    @GetMapping("{workspaceId}")
    public ResponseEntity<ResponseMessage<GetWorkspaceDTO>> getWorkspace(@PathVariable Long workspaceId) {
        GetWorkspaceDTO body = workspaceService.getWorkspaceById(workspaceId);
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
        GetWorkspaceDTO body = workspaceService.deactivateWorkspace(workspaceId);
        return ResponseMessageBuilder.build(body, HttpStatus.OK);
    }

    /**
     * Activates a workspace.
     *
     * @param workspaceId The ID of the workspace to activate.
     * @return A ResponseEntity containing a ResponseMessage with the activated workspace information on success.
     */
    @PatchMapping("{workspaceId}/activate")
    public ResponseEntity<ResponseMessage<GetWorkspaceDTO>> activateWorkspace(@PathVariable Long workspaceId) {
        GetWorkspaceDTO body = workspaceService.activateWorkspace(workspaceId);
        return ResponseMessageBuilder.build(body, HttpStatus.OK);
    }

    /**
     * Updates workspace information.
     *
     * @param workspaceId        The ID of the workspace to update.
     * @param updateWorkspaceDTO The DTO containing updated workspace information.
     * @return A ResponseEntity containing a ResponseMessage with the updated workspace information on success.
     */
    @PutMapping("{workspaceId}/update")
    public ResponseEntity<ResponseMessage<GetWorkspaceDTO>> updateWorkspace(@PathVariable Long workspaceId, @RequestBody UpdateWorkspaceDTO updateWorkspaceDTO) {
        GetWorkspaceDTO body = workspaceService.updateWorkspace(workspaceId, updateWorkspaceDTO);
        return ResponseMessageBuilder.build(body, HttpStatus.OK);
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

    /**
     * Removes a member from a workspace.
     *
     * @param workspaceId The ID of the workspace.
     * @param userId      The ID of the user to remove.
     * @return A ResponseEntity containing a ResponseMessage indicating success on removal.
     */
    @DeleteMapping("{workspaceId}/members")
    public ResponseEntity<ResponseMessage<Void>> removeMember(@PathVariable Long workspaceId, @RequestParam Long userId) {
        workspaceMemberService.removeUserFromWorkspace(workspaceId, userId);
        return ResponseMessageBuilder.build(HttpStatus.OK);
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