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

    public PaginatedResponse<GetWorkspaceMemberDTO> getAllMembersByWorkspaceId(Long workspaceId, PaginationInfo paginationInfo) {
        return workspaceMemberDAO.getAllMembersByWorkspaceId(workspaceId, paginationInfo);
    }

    public void clockIn(Long workspaceId, Long userId) {
        jpaRepository.updateWorkspaceMemberByWorkspaceIdAndUserIdSetClockedIn(workspaceId, userId, true);
    }

    public void clockOut(Long workspaceId, Long userId) {
        jpaRepository.updateWorkspaceMemberByWorkspaceIdAndUserIdSetClockedIn(workspaceId, userId, false);
    }
}
