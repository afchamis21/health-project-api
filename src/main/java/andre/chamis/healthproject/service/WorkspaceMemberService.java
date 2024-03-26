package andre.chamis.healthproject.service;

import andre.chamis.healthproject.domain.exception.BadArgumentException;
import andre.chamis.healthproject.domain.exception.ForbiddenException;
import andre.chamis.healthproject.domain.request.PaginationInfo;
import andre.chamis.healthproject.domain.response.ErrorMessage;
import andre.chamis.healthproject.domain.response.PaginatedResponse;
import andre.chamis.healthproject.domain.user.dto.GetUserDTO;
import andre.chamis.healthproject.domain.user.model.User;
import andre.chamis.healthproject.domain.workspace.member.dto.CreateWorkspaceMemberDTO;
import andre.chamis.healthproject.domain.workspace.member.dto.GetWorkspaceMemberDTO;
import andre.chamis.healthproject.domain.workspace.member.model.WorkspaceMember;
import andre.chamis.healthproject.domain.workspace.member.repository.WorkspaceMemberRepository;
import andre.chamis.healthproject.domain.workspace.model.Workspace;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkspaceMemberService {
    private final UserService userService;
    private final WorkspaceService workspaceService;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    /**
     * Adds a user to a workspace.
     *
     * @param workspaceId              The ID of the workspace.
     * @param createWorkspaceMemberDTO The DTO containing the email of the user to add.
     * @return The details of the added workspace member.
     * @throws ForbiddenException   If the workspace is not active.
     * @throws BadArgumentException If the user is already a member of the workspace.
     */
    public GetWorkspaceMemberDTO addUserToWorkspace(Long workspaceId, CreateWorkspaceMemberDTO createWorkspaceMemberDTO) {
        Workspace workspace = workspaceService.getWorkspaceByIdOrThrow(workspaceId);

        workspaceService.checkWorkspaceOwnership(workspace);

        String email = createWorkspaceMemberDTO.email();

        log.info("Getting user with email [{}] or registering a new one!", email);

        User user = userService.findUserByEmail(email).orElseGet(() -> {
            log.warn("Creating a new user with email [{}]", email);
            return userService.createUser(email, Optional.empty());
        });

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

        WorkspaceMember workspaceMember = new WorkspaceMember(workspaceId, user.getUserId());
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

    /**
     * Removes a user from a workspace.
     *
     * @param workspaceId The ID of the workspace.
     * @param userId      The ID of the user to remove.
     * @throws ForbiddenException If the workspace is not active.
     */
    public void removeUserFromWorkspace(Long workspaceId, Long userId) {
        Workspace workspace = workspaceService.getWorkspaceByIdOrThrow(workspaceId);

        log.info("Removing user [{}] from workspace [{}]", userId, workspaceId);

        workspaceService.checkWorkspaceOwnership(workspace);

        if (!workspace.isActive()) {
            log.warn("Workspace is deactivated");
            throw new ForbiddenException(ErrorMessage.INACTIVE_WORKSPACE);
        }

        workspaceMemberRepository.deleteByWorkspaceIdAndUserId(workspaceId, userId);
        log.info("User [{}] removed from workspace [{}]!", userId, workspaceId);
    }

    /**
     * Retrieves all members of a workspace.
     *
     * @param workspaceId    The ID of the workspace.
     * @param paginationInfo The pagination information.
     * @return Paginated list of workspace members.
     */
    public PaginatedResponse<GetWorkspaceMemberDTO> getAllMembersOfWorkspace(Long workspaceId, PaginationInfo paginationInfo) {
        log.info("Searching for all members of workspace [{}]. Pagination options: page [{}] size [{}]", workspaceId, paginationInfo.getPage(), paginationInfo.getSize());
        PaginatedResponse<GetWorkspaceMemberDTO> members = workspaceMemberRepository.getAllMembersByWorkspaceId(workspaceId, paginationInfo);

        log.info("Found members [{}]", members);

        return members;
    }

    /**
     * Checks if a user is a member of a workspace.
     *
     * @param workspaceId The ID of the workspace.
     * @param memberId    The ID of the user.
     * @return True if the user is a member of the workspace, otherwise false.
     */
    protected boolean isMemberOfWorkspace(Long workspaceId, Long memberId) {
        return workspaceMemberRepository.existsByWorkspaceIdAndUserId(workspaceId, memberId);
    }
}
