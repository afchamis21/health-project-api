package andre.chamis.healthproject.domain.workspace.repository;

import andre.chamis.healthproject.domain.workspace.dto.GetWorkspaceDTO;
import andre.chamis.healthproject.domain.workspace.model.Workspace;
import jakarta.persistence.SqlResultSetMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing workspace entities using JPA.
 */
@Repository
interface WorkspaceJpaRepository extends JpaRepository<Workspace, Long> {
    boolean existsWorkspaceByWorkspaceIdAndOwnerId(Long workspaceId, Long ownerId);
// TODO essa merda está retornando 1 para quando é o dono e 2 para quando não é. Se encher mt o saco retorna uma lista e pega o primeiro que o id da workspace e do user bater
    @Query(value = """
                SELECT new andre.chamis.healthproject.domain.workspace.dto.GetWorkspaceDTO(w.workspaceId, CONCAT(p.name, COALESCE(' ' || p.surname, '')), w.ownerId, w.patientId, w.active, w.createDt)
                FROM Workspace w
                    JOIN WorkspaceMember wu ON w.workspaceId = wu.workspaceId
                    JOIN Patient p ON w.patientId = p.patientId
                WHERE w.workspaceId = :workspaceId
                    AND (w.ownerId = wu.userId OR (wu.userId = :userId AND wu.active = true))
""")
    List<GetWorkspaceDTO> getWorkspaceDtoByIdIfOwnerOrMemberAndActive(Long workspaceId, Long userId);
}
