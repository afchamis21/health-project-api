package andre.chamis.healthproject.domain.workspace.member.repository;

import andre.chamis.healthproject.domain.workspace.member.model.WorkspaceMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface WorkspaceMemberJpaRepository extends JpaRepository<WorkspaceMember, Long> {

    /**
     * Checks if a workspace member with the given workspace ID and user ID exists.
     *
     * @param workspaceId The ID of the workspace.
     * @param userId      The ID of the user.
     * @return {@code true} if a workspace member exists, otherwise {@code false}.
     */
    boolean existsByWorkspaceIdAndUserId(Long workspaceId, Long userId);

    /**
     * Deletes all workspace members by the given workspace ID.
     *
     * @param workspaceId The ID of the workspace.
     */
    void deleteAllByWorkspaceId(Long workspaceId);

    /**
     * Deletes a workspace member by the given workspace ID and user ID.
     *
     * @param workspaceId The ID of the workspace.
     * @param userId      The ID of the user.
     */
    void deleteByWorkspaceIdAndUserId(Long workspaceId, Long userId);
}
