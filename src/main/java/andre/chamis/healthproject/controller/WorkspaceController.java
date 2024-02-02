package andre.chamis.healthproject.controller;

import andre.chamis.healthproject.domain.auth.annotation.JwtAuthenticated;
import andre.chamis.healthproject.domain.response.ResponseMessage;
import andre.chamis.healthproject.domain.response.ResponseMessageBuilder;
import andre.chamis.healthproject.domain.workspace.dto.CreateWorkspaceDTO;
import andre.chamis.healthproject.domain.workspace.dto.GetWorkspaceDTO;
import andre.chamis.healthproject.domain.workspace.dto.UpdateWorkspaceDTO;
import andre.chamis.healthproject.domain.workspace.member.dto.GetWorkspaceMemberDTO;
import andre.chamis.healthproject.domain.workspace.member.dto.GetWorkspaceMembersDTO;
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

    @PostMapping("create")
    public ResponseEntity<ResponseMessage<GetWorkspaceDTO>> createWorkspace(@RequestBody CreateWorkspaceDTO createWorkspaceDTO) {
        GetWorkspaceDTO body = workspaceService.createWorkspace(createWorkspaceDTO);
        return ResponseMessageBuilder.build(body, HttpStatus.CREATED);
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
    public ResponseEntity<ResponseMessage<GetWorkspaceMembersDTO>> getMembers(
            @PathVariable Long workspaceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        GetWorkspaceMembersDTO body = workspaceService.getAllMembersOfWorkspace(workspaceId, page, size);
        return ResponseMessageBuilder.build(body, HttpStatus.OK);
    }

    @PostMapping("{workspaceId}/members")
    public ResponseEntity<ResponseMessage<GetWorkspaceMemberDTO>> addMember(@PathVariable Long workspaceId, @RequestParam String email) {
        GetWorkspaceMemberDTO body = workspaceService.addUserToWorkspace(workspaceId, email);
        return ResponseMessageBuilder.build(body, HttpStatus.OK);
    }

    @DeleteMapping("{workspaceId}/members")
    public ResponseEntity<ResponseMessage<Void>> removeMember(@PathVariable Long workspaceId, @RequestParam Long userId) {
        workspaceService.removeUserFromWorkspace(workspaceId, userId);
        return ResponseMessageBuilder.build(HttpStatus.OK);
    }
}