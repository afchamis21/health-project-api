package andre.chamis.healthproject.domain.workspace.member.repository;

import andre.chamis.healthproject.domain.workspace.member.dto.GetWorkspaceMembersDTO;
import andre.chamis.healthproject.domain.workspace.member.model.WorkspaceMember;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class WorkspaceMemberRepository {
    private final WorkspaceMemberDAO workspaceMemberDAO;
    private final WorkspaceMemberJpaRepository jpaRepository;

    public WorkspaceMember save(WorkspaceMember workspaceMember) {
        return jpaRepository.save(workspaceMember);
    }

    public boolean existsByWorkspaceIdAndUserId(Long workspaceId, Long userId) {
        return jpaRepository.existsByWorkspaceIdAndUserId(workspaceId, userId);
    }

    public void deleteAllByWorkspaceId(Long workspaceId) {
        jpaRepository.deleteAllByWorkspaceId(workspaceId);
    }

    @Transactional
    public void deleteByWorkspaceIdAndUserId(Long workspaceId, Long userId) {
        jpaRepository.deleteByWorkspaceIdAndUserId(workspaceId, userId);
    }

    public GetWorkspaceMembersDTO getAllMembersByWorkspaceId(Long workspaceId, int page, int size) {
        return workspaceMemberDAO.getAllMembersByWorkspaceId(workspaceId, page, size);
    }
}
