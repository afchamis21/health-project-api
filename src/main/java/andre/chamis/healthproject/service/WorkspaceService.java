package andre.chamis.healthproject.service;

import andre.chamis.healthproject.context.ServiceContext;
import andre.chamis.healthproject.domain.exception.BadArgumentException;
import andre.chamis.healthproject.domain.exception.ForbiddenException;
import andre.chamis.healthproject.domain.response.ErrorMessage;
import andre.chamis.healthproject.domain.workspace.dto.CreateWorkspaceDTO;
import andre.chamis.healthproject.domain.workspace.dto.GetWorkspaceDTO;
import andre.chamis.healthproject.domain.workspace.dto.UpdateWorkspaceDTO;
import andre.chamis.healthproject.domain.workspace.member.model.WorkspaceMember;
import andre.chamis.healthproject.domain.workspace.member.repository.WorkspaceMemberRepository;
import andre.chamis.healthproject.domain.workspace.model.Workspace;
import andre.chamis.healthproject.domain.workspace.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

// TODO dividir em dois serviços (workspaces e members), e fazer a parte de ativar e desativar temporariamente os
//  workspace members

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkspaceService {
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    public GetWorkspaceDTO createWorkspace(CreateWorkspaceDTO createWorkspaceDTO) {
        Long currentUserId = ServiceContext.getContext().getUserId();

        log.info("Creating workspace with name [{}] and ownerId [{}]", createWorkspaceDTO.name(), currentUserId);

        Workspace workspace = new Workspace();
        workspace.setWorkspaceName(createWorkspaceDTO.name());
        workspace.setOwnerId(currentUserId);
        workspace.setCreateDt(Date.from(Instant.now()));
        workspace.setActive(true);

        workspace = workspaceRepository.save(workspace);

        log.info("Added workspace to database [{}]", workspace);

        log.info("Adding owner as member of workspace!");
        WorkspaceMember member = new WorkspaceMember(workspace.getWorkspaceId(), currentUserId);
        workspaceMemberRepository.save(member);
        log.info("Owner saved to database");

        return GetWorkspaceDTO.fromWorkspace(workspace);
    }

    public void deleteWorkspace(Long workspaceId) {
        log.info("Preparing to delete workspace [{}]", workspaceId);
        Workspace workspace = getWorkspaceByIdOrThrow(workspaceId);

        checkWorkspaceOwnership(workspace);

        if (workspace.isActive()) {
            log.warn("Workspace [{}] is deactivated!", workspaceId);
            throw new ForbiddenException(ErrorMessage.CAN_NOT_DELETE_ACTIVE_WORKSPACE);
        }

        workspaceRepository.deleteByWorkspaceId(workspaceId);
        log.info("Workspace [{}] permanently deleted!", workspaceId);

        workspaceMemberRepository.deleteAllByWorkspaceId(workspaceId);
        log.info("Deleted all members of workspace [{}]", workspaceId);
    }

    public GetWorkspaceDTO deactivateWorkspace(Long workspaceId) {
        Workspace workspace = getWorkspaceByIdOrThrow(workspaceId);

        checkWorkspaceOwnership(workspace);

        log.info("Deactivating workspace [{}]", workspaceId);

        workspace.setActive(false);
        workspaceRepository.save(workspace);

        log.info("Workspace [{}] deactivated!", workspaceId);

        return GetWorkspaceDTO.fromWorkspace(workspace);
    }

    public GetWorkspaceDTO activateWorkspace(Long workspaceId) {
        Workspace workspace = getWorkspaceByIdOrThrow(workspaceId);

        checkWorkspaceOwnership(workspace);

        log.info("Activating workspace [{}]", workspaceId);

        workspace.setActive(true);
        workspaceRepository.save(workspace);

        log.info("Workspace [{}] activated!", workspaceId);

        return GetWorkspaceDTO.fromWorkspace(workspace);
    }

    public Workspace getWorkspaceByIdOrThrow(Long workspaceId) {
        log.info("Searching for workspace with id [{}]", workspaceId);

        Workspace workspace = workspaceRepository.findByWorkspaceId(workspaceId)
                .orElseThrow(() -> new BadArgumentException(ErrorMessage.WORKSPACE_NOT_FOUND));

        log.debug("Workspace found! [{}]", workspace);

        return workspace;
    }

    public void checkWorkspaceOwnership(Workspace workspace) {
        log.info("Checking if logged in user is owner of workspace [{}]", workspace);
        Long currentUserId = ServiceContext.getContext().getUserId();

        if (currentUserId == null) {
            log.error("Checking ownership but theres no logged in user. Something happened");
            throw new ForbiddenException();
        }

        log.debug("Current user id [{}]", currentUserId);
        boolean isUserOwnerOfWorkspace = currentUserId.equals(workspace.getOwnerId());
        log.debug("Is user owner of workspace [{}]", isUserOwnerOfWorkspace);
        if (!isUserOwnerOfWorkspace) {
            throw new ForbiddenException(ErrorMessage.WORKSPACE_OWNERSHIP);
        }
    }

    public GetWorkspaceDTO updateWorkspace(Long workspaceId, UpdateWorkspaceDTO updateWorkspaceDTO) {
        Workspace workspace = getWorkspaceByIdOrThrow(workspaceId);

        checkWorkspaceOwnership(workspace);

        log.info("Updating workspace [{}] with params [{}]", workspaceId, updateWorkspaceDTO);

        if (!workspace.isActive()) {
            log.warn("Workspace [{}] is deactivated!", workspace);
            throw new ForbiddenException(ErrorMessage.INACTIVE_WORKSPACE);
        }

        boolean updated = false;

        if (null != updateWorkspaceDTO.name() && updateWorkspaceDTO.name().isBlank()) {
            workspace.setWorkspaceName(updateWorkspaceDTO.name());
            updated = true;
        }

        if (updated) {
            log.info("Workspace was updated! Saving to database");
            workspace = workspaceRepository.save(workspace);
        }

        return GetWorkspaceDTO.fromWorkspace(workspace);
    }
}
