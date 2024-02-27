package andre.chamis.healthproject.controller;

import andre.chamis.healthproject.domain.auth.annotation.JwtAuthenticated;
import andre.chamis.healthproject.domain.request.PaginationInfo;
import andre.chamis.healthproject.domain.response.PaginatedResponse;
import andre.chamis.healthproject.domain.response.ResponseMessage;
import andre.chamis.healthproject.domain.response.ResponseMessageBuilder;
import andre.chamis.healthproject.domain.workspace.dto.CreateWorkspaceDTO;
import andre.chamis.healthproject.domain.workspace.dto.GetWorkspaceDTO;
import andre.chamis.healthproject.domain.workspace.dto.UpdateWorkspaceDTO;
import andre.chamis.healthproject.domain.workspace.member.dto.CreateWorkspaceMemberDTO;
import andre.chamis.healthproject.domain.workspace.member.dto.GetWorkspaceMemberDTO;
import andre.chamis.healthproject.service.WorkspaceMemberService;
import andre.chamis.healthproject.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@JwtAuthenticated
@RequiredArgsConstructor
@RequestMapping("workspace")
public class WorkspaceController {
    private final WorkspaceService workspaceService;
    private final WorkspaceMemberService workspaceMemberService;

    @PostMapping("create")
    public ResponseEntity<ResponseMessage<GetWorkspaceDTO>> createWorkspace(@RequestBody CreateWorkspaceDTO createWorkspaceDTO) {
        GetWorkspaceDTO body = workspaceService.createWorkspace(createWorkspaceDTO);
        return ResponseMessageBuilder.build(body, HttpStatus.CREATED);
    }

    @GetMapping("{workspaceId}")
    public ResponseEntity<ResponseMessage<GetWorkspaceDTO>> getWorkspace(@PathVariable Long workspaceId) {
        GetWorkspaceDTO body = workspaceService.getWorkspaceById(workspaceId);
        return ResponseMessageBuilder.build(body, HttpStatus.OK);
    }

    @DeleteMapping("{workspaceId}/delete")
    public ResponseEntity<ResponseMessage<Void>> deleteWorkspace(@PathVariable Long workspaceId) {
        workspaceService.deleteWorkspace(workspaceId);
        return ResponseMessageBuilder.build(HttpStatus.OK);
    }

    @PatchMapping("{workspaceId}/deactivate")
    public ResponseEntity<ResponseMessage<GetWorkspaceDTO>> deactivateWorkspace(@PathVariable Long workspaceId) {
        GetWorkspaceDTO body = workspaceService.deactivateWorkspace(workspaceId);
        return ResponseMessageBuilder.build(body, HttpStatus.OK);
    }

    @PatchMapping("{workspaceId}/activate")
    public ResponseEntity<ResponseMessage<GetWorkspaceDTO>> activateWorkspace(@PathVariable Long workspaceId) {
        GetWorkspaceDTO body = workspaceService.activateWorkspace(workspaceId);
        return ResponseMessageBuilder.build(body, HttpStatus.OK);
    }

    @PutMapping("{workspaceId}/update")
    public ResponseEntity<ResponseMessage<GetWorkspaceDTO>> updateWorkspace(@PathVariable Long workspaceId, @RequestBody UpdateWorkspaceDTO updateWorkspaceDTO) {
        GetWorkspaceDTO body = workspaceService.updateWorkspace(workspaceId, updateWorkspaceDTO);
        return ResponseMessageBuilder.build(body, HttpStatus.OK);
    }

    @GetMapping("{workspaceId}/members")
    public ResponseEntity<ResponseMessage<PaginatedResponse<GetWorkspaceMemberDTO>>> getMembers(
            @PathVariable Long workspaceId, PaginationInfo paginationInfo

    ) {
        PaginatedResponse<GetWorkspaceMemberDTO> body = workspaceMemberService.getAllMembersOfWorkspace(workspaceId, paginationInfo);
        return ResponseMessageBuilder.build(body, HttpStatus.OK);
    }

    @PostMapping("{workspaceId}/members")
    public ResponseEntity<ResponseMessage<GetWorkspaceMemberDTO>> addMember(@PathVariable Long workspaceId, @RequestBody CreateWorkspaceMemberDTO createWorkspaceMemberDTO) {
        GetWorkspaceMemberDTO body = workspaceMemberService.addUserToWorkspace(workspaceId, createWorkspaceMemberDTO);
        return ResponseMessageBuilder.build(body, HttpStatus.OK);
    }

    @DeleteMapping("{workspaceId}/members")
    public ResponseEntity<ResponseMessage<Void>> removeMember(@PathVariable Long workspaceId, @RequestParam Long userId) {
        workspaceMemberService.removeUserFromWorkspace(workspaceId, userId);
        return ResponseMessageBuilder.build(HttpStatus.OK);
    }
}