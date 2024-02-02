package andre.chamis.healthproject.domain.workspace.repository;

import andre.chamis.healthproject.domain.workspace.dto.GetWorkspacesDTO;
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

    public GetWorkspacesDTO findWorkspacesByOwnerId(Long userId, int page, int size) {
        return workspaceDAO.getWorkspacesByOwnerId(userId, page, size);
    }
}
