package andre.chamis.healthproject.domain.workspace.repository;

import andre.chamis.healthproject.domain.workspace.model.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing workspace entities using JPA.
 */
@Repository
interface WorkspaceJpaRepository extends JpaRepository<Workspace, Long> {
    boolean existsWorkspaceByWorkspaceIdAndOwnerId(Long workspaceId, Long ownerId);
}
