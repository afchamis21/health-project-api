package andre.chamis.healthproject.service;

import andre.chamis.healthproject.context.ServiceContext;
import andre.chamis.healthproject.domain.exception.BadArgumentException;
import andre.chamis.healthproject.domain.exception.ForbiddenException;
import andre.chamis.healthproject.domain.response.ErrorMessage;
import andre.chamis.healthproject.domain.user.model.User;
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
import java.util.Objects;


@Slf4j
@Service
@RequiredArgsConstructor
public class WorkspaceService {
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final UserService userService;

    /**
     * Creates a new workspace.
     *
     * @param createWorkspaceDTO The DTO containing workspace creation details.
     * @return The details of the created workspace.
     * @throws ForbiddenException   If the current user is not a paid user.
     * @throws BadArgumentException If the workspace name is missing or invalid.
     */
    public GetWorkspaceDTO createWorkspace(CreateWorkspaceDTO createWorkspaceDTO) {
        User user = userService.findCurrentUser();
        Long currentUserId = user.getUserId();

        if (!user.isPaymentActive()) {
            throw new ForbiddenException(ErrorMessage.PAID_USER_ONLY);
        }

        log.info("Creating workspace with name [{}] and ownerId [{}]", createWorkspaceDTO.name(), currentUserId);

        if (null == createWorkspaceDTO.name()) {
            throw new BadArgumentException(ErrorMessage.MISSING_WORKSPACE_NAME);
        }

        if (createWorkspaceDTO.name().length() < 3 || createWorkspaceDTO.name().length() > 50) {
            throw new BadArgumentException(ErrorMessage.INVALID_WORKSPACE_NAME);
        }

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

    /**
     * Deletes a workspace.
     *
     * @param workspaceId The ID of the workspace to delete.
     * @throws ForbiddenException If the workspace is active.
     */
    public void deleteWorkspace(Long workspaceId) {
        log.info("Preparing to delete workspace [{}]", workspaceId);
        Workspace workspace = getWorkspaceAndCheckOwnership(workspaceId, false);

        if (workspace.isActive()) {
            log.warn("Workspace [{}] is deactivated!", workspaceId);
            throw new ForbiddenException(ErrorMessage.CAN_NOT_DELETE_ACTIVE_WORKSPACE);
        }

        workspaceRepository.deleteByWorkspaceId(workspaceId);
        log.info("Workspace [{}] permanently deleted!", workspaceId);

        workspaceMemberRepository.deleteAllByWorkspaceId(workspaceId);
        log.info("Deleted all members of workspace [{}]", workspaceId);
    }

    /**
     * Deactivates a workspace.
     *
     * @param workspaceId The ID of the workspace to deactivate.
     * @return The details of the deactivated workspace.
     */
    public GetWorkspaceDTO deactivateWorkspace(Long workspaceId) {
        Workspace workspace = getWorkspaceAndCheckOwnership(workspaceId, false);

        log.info("Deactivating workspace [{}]", workspaceId);

        workspace.setActive(false);
        workspaceRepository.save(workspace);

        log.info("Workspace [{}] deactivated!", workspaceId);

        return GetWorkspaceDTO.fromWorkspace(workspace);
    }

    /**
     * Activates a workspace.
     *
     * @param workspaceId The ID of the workspace to activate.
     * @return The details of the activated workspace.
     */
    public GetWorkspaceDTO activateWorkspace(Long workspaceId) {
        Workspace workspace = getWorkspaceAndCheckOwnership(workspaceId, false);

        log.info("Activating workspace [{}]", workspaceId);

        workspace.setActive(true);
        workspaceRepository.save(workspace);

        log.info("Workspace [{}] activated!", workspaceId);

        return GetWorkspaceDTO.fromWorkspace(workspace);
    }

    /**
     * Retrieves a workspace by ID or throws an exception if not found.
     *
     * @param workspaceId The ID of the workspace.
     * @return The retrieved workspace.
     * @throws BadArgumentException If the workspace is not found.
     */
    public Workspace getWorkspaceByIdOrThrow(Long workspaceId) {
        log.info("Searching for workspace with id [{}]", workspaceId);

        Workspace workspace = workspaceRepository.findByWorkspaceId(workspaceId)
                .orElseThrow(() -> new BadArgumentException(ErrorMessage.WORKSPACE_NOT_FOUND));

        log.debug("Workspace found! [{}]", workspace);

        return workspace;
    }

    /**
     * Retrieves a workspace by ID or throws an exception if not found or if workspace is not active.
     *
     * @param workspaceId The ID of the workspace.
     * @return The retrieved workspace.
     * @throws BadArgumentException If the workspace is not found.
     * @throws ForbiddenException   If the workspace is not active.
     */
    public Workspace getWorkspaceIfActiveOrThrow(Long workspaceId) {
        Workspace workspace = getWorkspaceByIdOrThrow(workspaceId);

        if (!workspace.isActive()) {
            log.error("Workspace [{}] is not active!", workspaceId);
            throw new ForbiddenException(ErrorMessage.INACTIVE_WORKSPACE);
        }

        return workspace;
    }

    /**
     * Gets a workspace verifying if the authenticated user is its owner and if it's active.
     *
     * @param workspaceId         The ID of the workspace to.
     * @param activeWorkspaceOnly Weather to check if the workspace is active.
     * @throws BadArgumentException If the workspace is not found.
     * @throws ForbiddenException   If the current user is not the owner of the workspace or if the workspace is not active.
     */
    public Workspace getWorkspaceAndCheckOwnership(Long workspaceId, boolean activeWorkspaceOnly) {
        Workspace workspace = activeWorkspaceOnly
                ? getWorkspaceIfActiveOrThrow(workspaceId) : getWorkspaceByIdOrThrow(workspaceId);

        checkWorkspaceOwnership(workspace);

        return workspace;
    }

    /**
     * Checks if the current user is the owner of the workspace.
     *
     * @param workspace The workspace to check.
     * @throws ForbiddenException If the current user is not the owner of the workspace.
     */
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

    /**
     * Updates a workspace.
     *
     * @param workspaceId        The ID of the workspace to update.
     * @param updateWorkspaceDTO The DTO containing workspace update details.
     * @return The details of the updated workspace.
     * @throws ForbiddenException   If the workspace is inactive or if the current user is not the owner of the workspace.
     * @throws BadArgumentException If the updated workspace name is blank.
     */
    public GetWorkspaceDTO updateWorkspace(Long workspaceId, UpdateWorkspaceDTO updateWorkspaceDTO) {
        Workspace workspace = getWorkspaceAndCheckOwnership(workspaceId, true);

        log.info("Updating workspace [{}] with params [{}]", workspaceId, updateWorkspaceDTO);

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

    /**
     * Retrieves a workspace by ID.
     *
     * @param workspaceId The ID of the workspace.
     * @return The details of the retrieved workspace.
     * @throws ForbiddenException If the user is not a member or owner of the workspace.
     */
    public GetWorkspaceDTO getWorkspaceById(Long workspaceId) {
        Workspace workspace = getWorkspaceByIdOrThrow(workspaceId);

        boolean isUserMemberOfWorkspace = workspaceMemberRepository.existsByWorkspaceIdAndUserId(
                workspaceId,
                ServiceContext.getContext().getUserId()
        );

        if (!isUserMemberOfWorkspace && !Objects.equals(workspace.getOwnerId(), ServiceContext.getContext().getUserId())) {
            throw new ForbiddenException(ErrorMessage.INVALID_WORKSPACE_ACCESS);
        }

        return GetWorkspaceDTO.fromWorkspace(workspace);
    }
}
