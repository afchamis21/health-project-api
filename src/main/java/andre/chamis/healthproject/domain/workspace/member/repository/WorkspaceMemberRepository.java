package andre.chamis.healthproject.domain.workspace.member.repository;

import andre.chamis.healthproject.domain.request.PaginationInfo;
import andre.chamis.healthproject.domain.response.PaginatedResponse;
import andre.chamis.healthproject.domain.workspace.member.dto.GetWorkspaceMemberDTO;
import andre.chamis.healthproject.domain.workspace.member.model.WorkspaceMember;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
     * Deletes a workspace member by the given workspace ID and user ID.
     *
     * @param workspaceId The ID of the workspace.
     * @param userId      The ID of the user.
     */
    @Transactional
    public void deleteByWorkspaceIdAndUserId(Long workspaceId, Long userId) {
        jpaRepository.deleteByWorkspaceIdAndUserId(workspaceId, userId);
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
}
