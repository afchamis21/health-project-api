package andre.chamis.healthproject.service;

import andre.chamis.healthproject.context.ServiceContext;
import andre.chamis.healthproject.domain.exception.BadArgumentException;
import andre.chamis.healthproject.domain.exception.ForbiddenException;
import andre.chamis.healthproject.domain.patient.dto.CreatePatientDTO;
import andre.chamis.healthproject.domain.patient.model.Patient;
import andre.chamis.healthproject.domain.response.ErrorMessage;
import andre.chamis.healthproject.domain.user.model.User;
import andre.chamis.healthproject.domain.workspace.dto.GetWorkspaceDTO;
import andre.chamis.healthproject.domain.workspace.member.model.WorkspaceMember;
import andre.chamis.healthproject.domain.workspace.member.repository.WorkspaceMemberRepository;
import andre.chamis.healthproject.domain.workspace.model.Workspace;
import andre.chamis.healthproject.domain.workspace.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;


@Slf4j
@Service
@RequiredArgsConstructor
public class WorkspaceService {
    private final UserService userService;
    private final PatientService patientService;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    /**
     * Creates a new workspace.
     *
     * @param createPatientDTO The DTO containing workspace creation details.
     * @return The details of the created workspace.
     * @throws ForbiddenException   If the current user is not a paid user.
     * @throws BadArgumentException If the workspace name is missing or invalid.
     */
    public GetWorkspaceDTO createWorkspace(CreatePatientDTO createPatientDTO) {
        User user = userService.findCurrentUser();
        Long currentUserId = user.getUserId();

        if (!user.isPaymentActive()) {
            throw new ForbiddenException(ErrorMessage.PAID_USER_ONLY);
        }

        log.info("Creating workspace with name [{}] and ownerId [{}]", createPatientDTO.name(), currentUserId);

        if (null == createPatientDTO.name()) {
            throw new BadArgumentException(ErrorMessage.MISSING_WORKSPACE_NAME);
        }

        if (createPatientDTO.name().length() < 3 || createPatientDTO.name().length() > 50) {
            throw new BadArgumentException(ErrorMessage.INVALID_WORKSPACE_NAME);
        }

        Patient patient = patientService.createPatient(createPatientDTO);

        Workspace workspace = new Workspace();
        workspace.setOwnerId(currentUserId);
        workspace.setPatientId(patient.getPatientId());
        workspace.setCreateDt(Date.from(Instant.now()));
        workspace.setActive(true);

        workspace = workspaceRepository.save(workspace);

        log.info("Added workspace to database [{}]", workspace);

        log.info("Adding owner as member of workspace!");
        WorkspaceMember member = new WorkspaceMember(workspace.getWorkspaceId(), currentUserId);
        workspaceMemberRepository.save(member);
        log.info("Owner saved to database");

        String workspaceName = patient.getSurname() != null && !patient.getSurname().isBlank()
                ? patient.getName() + " " + patient.getSurname()
                : patient.getName();

        return new GetWorkspaceDTO(workspace, workspaceName);
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
     */
    public void deactivateWorkspace(Long workspaceId) {
        Workspace workspace = getWorkspaceAndCheckOwnership(workspaceId, false);

        log.info("Deactivating workspace [{}]", workspaceId);

        workspace.setActive(false);
        workspaceRepository.save(workspace);

        log.info("Workspace [{}] deactivated!", workspaceId);
    }

    /**
     * Activates a workspace.
     *
     * @param workspaceId The ID of the workspace to activate.
     */
    public void activateWorkspace(Long workspaceId) {
        Workspace workspace = getWorkspaceAndCheckOwnership(workspaceId, false);

        log.info("Activating workspace [{}]", workspaceId);

        workspace.setActive(true);
        workspaceRepository.save(workspace);

        log.info("Workspace [{}] activated!", workspaceId);
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

        checkWorkspaceOwnershipOrThrow(workspace);

        return workspace;
    }

    /**
     * Checks if the current user is the owner of the workspace.
     *
     * @param workspace The workspace to check.
     * @throws ForbiddenException If the current user is not the owner of the workspace.
     */
    private void checkWorkspaceOwnershipOrThrow(Workspace workspace) {
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
     * Checks if current user is owner of a workspace and throws a {@code ForbiddenException}
     * if it isn't
     *
     * @param workspaceId The ID of the workspace to be checked.
     * @throws ForbiddenException If user is not owner of workspace.
     */
    public void checkWorkspaceOwnershipOrThrow(Long workspaceId) {
        Long currentUserId = ServiceContext.getContext().getUserId();
        boolean isWorkspaceOwner = workspaceRepository.existsWorkspaceByWorkspaceIdAndOwnerId(workspaceId, currentUserId);

        if (!isWorkspaceOwner) {
            throw new ForbiddenException(ErrorMessage.WORKSPACE_OWNERSHIP);
        }
    }
}
