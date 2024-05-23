package andre.chamis.healthproject.domain.workspace.repository;

import andre.chamis.healthproject.domain.workspace.dto.GetWorkspaceDTO;
import andre.chamis.healthproject.domain.workspace.model.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing workspace entities using JPA.
 */
@Repository
interface WorkspaceJpaRepository extends JpaRepository<Workspace, Long> {
    boolean existsWorkspaceByWorkspaceIdAndOwnerId(Long workspaceId, Long ownerId);

    @Query(value = """
                    SELECT DISTINCT new andre.chamis.healthproject.domain.workspace.dto.GetWorkspaceDTO(
                        w.workspaceId,\s
                        CONCAT(p.name, COALESCE(' ' || p.surname, '')),\s
                        w.ownerId,\s
                        w.patientId,\s
                        w.active,\s
                        w.createDt
                    )
                    FROM Workspace w
                    JOIN WorkspaceMember wu ON w.workspaceId = wu.workspaceId
                    JOIN Patient p ON w.patientId = p.patientId
                    WHERE w.workspaceId = :workspaceId
                    AND (
                        w.ownerId = :userId OR\s
                        (wu.userId = :userId AND wu.active = true)
                    )
            """)
    Optional<GetWorkspaceDTO> getWorkspaceDtoByIdIfOwnerOrMemberAndActive(Long workspaceId, Long userId);
}
