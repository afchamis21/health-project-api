package andre.chamis.healthproject.service;

import andre.chamis.healthproject.context.ServiceContext;
import andre.chamis.healthproject.domain.exception.BadArgumentException;
import andre.chamis.healthproject.domain.exception.ForbiddenException;
import andre.chamis.healthproject.domain.response.ErrorMessage;
import andre.chamis.healthproject.domain.user.dto.GetUserDTO;
import andre.chamis.healthproject.domain.user.model.User;
import andre.chamis.healthproject.domain.workspace.dto.CreateWorkspaceDTO;
import andre.chamis.healthproject.domain.workspace.dto.GetWorkspaceDTO;
import andre.chamis.healthproject.domain.workspace.dto.UpdateWorkspaceDTO;
import andre.chamis.healthproject.domain.workspace.member.dto.GetWorkspaceMemberDTO;
import andre.chamis.healthproject.domain.workspace.member.dto.GetWorkspaceMembersDTO;
import andre.chamis.healthproject.domain.workspace.member.model.WorkspaceMember;
import andre.chamis.healthproject.domain.workspace.member.repository.WorkspaceMemberRepository;
import andre.chamis.healthproject.domain.workspace.model.Workspace;
import andre.chamis.healthproject.domain.workspace.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

// TODO dividir em dois serviços (workspaces e members), e fazer a parte de ativar e desativar temporariamente os
//  workspace members

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkspaceService {
    private final UserService userService;
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

        addUserToWorkspace(workspace, currentUserId);

        return GetWorkspaceDTO.fromWorkspace(workspace);
    }

    public void addUserToWorkspace(Workspace workspace, Long userId) {
        checkWorkspaceOwnership(workspace);

        User user = userService.findUserById(userId)
                .orElseThrow(() -> new BadArgumentException(ErrorMessage.USER_NOT_FOUND));

        addUserToWorkspace(workspace, user);
    }

    public GetWorkspaceMemberDTO addUserToWorkspace(Long workspaceId, String email) {
        Workspace workspace = getWorkspaceByIdOrThrow(workspaceId);

        checkWorkspaceOwnership(workspace);

        log.info("Getting user with email [{}] or registering a new one!", email);

        User user = userService.findUserByEmail(email).orElseGet(() -> userService.createUser(email, Optional.empty()));

        log.debug("User [{}]", user);

        return addUserToWorkspace(workspace, user);
    }

    public GetWorkspaceMemberDTO addUserToWorkspace(Workspace workspace, User user) {
        log.debug("Checking if user [{}] already is member of workspace [{}]!", user.getUserId(), workspace.getWorkspaceId());
        if (workspaceMemberRepository.existsByWorkspaceIdAndUserId(workspace.getWorkspaceId(), user.getUserId())) {
            log.warn("User already is member of workspace!");
            throw new BadArgumentException(ErrorMessage.USER_ALREADY_MEMBER);
        }
        log.debug("Check passed!");

        if (!workspace.isActive()) {
            log.warn("Workspace is not active!");
            throw new ForbiddenException(ErrorMessage.INACTIVE_WORKSPACE);
        }

        WorkspaceMember workspaceMember = new WorkspaceMember();

        workspaceMember.setWorkspaceId(workspace.getWorkspaceId());
        workspaceMember.setUserId(user.getUserId());
        workspaceMember.setCreateDt(Date.from(Instant.now()));
        workspaceMember.setActive(true);

        log.debug("Created workspace member [{}]", workspaceMember);

        workspaceMemberRepository.save(workspaceMember);

        log.info("Added workspace member to database [{}]", workspaceMember);

        return new GetWorkspaceMemberDTO(
                workspace.getWorkspaceId(),
                workspaceMember.isActive(),
                workspaceMember.getCreateDt(),
                GetUserDTO.fromUser(user)
        );
    }

    public void removeUserFromWorkspace(Long workspaceId, Long userId) {
        Workspace workspace = getWorkspaceByIdOrThrow(workspaceId);

        log.info("Removing user [{}] from workspace [{}]", userId, workspaceId);

        checkWorkspaceOwnership(workspace);

        if (!workspace.isActive()) {
            log.warn("Workspace is deactivated");
            throw new ForbiddenException(ErrorMessage.INACTIVE_WORKSPACE);
        }

        workspaceMemberRepository.deleteByWorkspaceIdAndUserId(workspaceId, userId);
        log.info("User [{}] removed from workspace [{}]!", userId, workspaceId);
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

    private void checkWorkspaceOwnership(Workspace workspace) {
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

    public GetWorkspaceMembersDTO getAllMembersOfWorkspace(Long workspaceId, int page, int size) {
        log.info("Searching for all members of workspace [{}]. Pagination options: page [{}] size [{}]", workspaceId, page, size);
        GetWorkspaceMembersDTO members = workspaceMemberRepository.getAllMembersByWorkspaceId(workspaceId, page, size);

        log.info("Found members [{}]", members);

        return members;
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
