package andre.chamis.healthproject.domain.workspace.repository;

import andre.chamis.healthproject.domain.request.PaginationInfo;
import andre.chamis.healthproject.domain.response.PaginatedResponse;
import andre.chamis.healthproject.domain.workspace.dto.GetWorkspaceDTO;
import andre.chamis.healthproject.domain.workspace.model.Workspace;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WorkspaceRepository {
    private final WorkspaceDAO workspaceDAO;
    private final WorkspaceJpaRepository jpaRepository;

    public Workspace save(Workspace workspace) {
        workspace.setUpdateDt(Date.from(Instant.now()));
        return jpaRepository.save(workspace);
    }

    public Optional<Workspace> findByWorkspaceId(Long workspaceId) {
        return jpaRepository.findById(workspaceId);
    }


    public void deleteByWorkspaceId(Long workspaceId) {
        jpaRepository.deleteById(workspaceId);
    }

    public PaginatedResponse<GetWorkspaceDTO> findWorkspacesByOwnerId(Long userId, PaginationInfo paginationInfo) {
        return workspaceDAO.getWorkspacesByOwnerId(userId, paginationInfo);
    }

    public PaginatedResponse<GetWorkspaceDTO> searchWorkspacesByNameAndMemberId(Long userId, String name, PaginationInfo paginationInfo) {
        return workspaceDAO.searchWorkspacesByNameAndMemberId(userId, name, paginationInfo);
    }
}
