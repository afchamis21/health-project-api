package andre.chamis.healthproject.domain.workspace.member.repository;

import andre.chamis.healthproject.domain.workspace.member.model.WorkspaceMember;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
interface WorkspaceMemberJpaRepository extends JpaRepository<WorkspaceMember, Long> {
    boolean existsByWorkspaceIdAndUserId(Long workspaceId, Long userId);

    void deleteAllByWorkspaceId(Long workspaceId);

    void deleteByWorkspaceIdAndUserId(Long workspaceId, Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE WorkspaceMember wm SET wm.clockedIn = :clockedIn WHERE wm.workspaceId = :workspaceId AND wm.userId = :userId")
    void updateWorkspaceMemberByWorkspaceIdAndUserIdSetClockedIn(Long workspaceId, Long userId, boolean clockedIn);
}
