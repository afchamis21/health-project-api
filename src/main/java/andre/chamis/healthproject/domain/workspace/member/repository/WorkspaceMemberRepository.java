package andre.chamis.healthproject.domain.workspace.member.repository;

import andre.chamis.healthproject.domain.request.PaginationInfo;
import andre.chamis.healthproject.domain.response.PaginatedResponse;
import andre.chamis.healthproject.domain.user.dto.GetUsernameAndIdDTO;
import andre.chamis.healthproject.domain.workspace.member.dto.GetWorkspaceMemberDTO;
import andre.chamis.healthproject.domain.workspace.member.model.WorkspaceMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WorkspaceMemberRepository {
    private final WorkspaceMemberDAO workspaceMemberDAO;
    private final WorkspaceMemberJpaRepository jpaRepository;

    /**
     * Saves a workspace member.
     *
     * @param workspaceMember The workspace member to save.
     * @return The saved workspace member.
     */
    public WorkspaceMember save(WorkspaceMember workspaceMember) {
        return jpaRepository.save(workspaceMember);
    }

    /**
     * Checks if a workspace member exists by the given workspace ID and user ID.
     *
     * @param workspaceId The ID of the workspace.
     * @param userId      The ID of the user.
     * @return {@code true} if a workspace member exists, otherwise {@code false}.
     */
    public boolean existsByWorkspaceIdAndUserId(Long workspaceId, Long userId) {
        return jpaRepository.existsByWorkspaceIdAndUserId(workspaceId, userId);
    }

    /**
     * Deletes all workspace members by the given workspace ID.
     *
     * @param workspaceId The ID of the workspace.
     */
    public void deleteAllByWorkspaceId(Long workspaceId) {
        jpaRepository.deleteAllByWorkspaceId(workspaceId);
    }

    /**
     * Retrieves all members of a workspace by the workspace ID.
     *
     * @param workspaceId    The ID of the workspace.
     * @param paginationInfo The pagination information.
     * @return A paginated response containing the workspace members.
     */
    public PaginatedResponse<GetWorkspaceMemberDTO> getAllMembersByWorkspaceId(Long workspaceId, PaginationInfo paginationInfo) {
        return workspaceMemberDAO.getAllMembersByWorkspaceId(workspaceId, paginationInfo);
    }

    /**
     * Finds a workspace member by its userId and the workspace id.
     *
     * @param workspaceId The ID of the workspace.
     * @param userId      The ID of the user.
     * @return An optional with the Workspace Member or empty if it's not found.
     */
    public Optional<WorkspaceMember> findByWorkspaceIdAndUserId(Long workspaceId, Long userId) {
        return jpaRepository.findByWorkspaceIdAndUserId(workspaceId, userId);
    }

    /**
     * Updates the isActive flag of a workspace member.
     *
     * @param workspaceId The ID of the workspace.
     * @param userId      The ID of the user.
     * @param active      The new value of the isActive flag.
     */
    public void updateWorkspaceMemberSetActive(Long workspaceId, Long userId, boolean active) {
        jpaRepository.updateWorkspaceMemberByWorkspaceIdAndUserIdSetActive(workspaceId, userId, active);
    }

    public List<GetUsernameAndIdDTO> getAllMemberNamesByWorkspaceId(Long workspaceId) {
        return workspaceMemberDAO.getAllMemberNamesByWorkspaceId(workspaceId);
    }
}
