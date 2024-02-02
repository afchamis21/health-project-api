package andre.chamis.healthproject.domain.workspace.member.repository;

import andre.chamis.healthproject.domain.workspace.member.model.WorkspaceMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface WorkspaceMemberJpaRepository extends JpaRepository<WorkspaceMember, Long> {
    boolean existsByWorkspaceIdAndUserId(Long workspaceId, Long userId);

    void deleteAllByWorkspaceId(Long workspaceId);

    void deleteByWorkspaceIdAndUserId(Long workspaceId, Long userId);
}
