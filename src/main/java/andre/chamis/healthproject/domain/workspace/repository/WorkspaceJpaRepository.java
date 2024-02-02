package andre.chamis.healthproject.domain.workspace.repository;

import andre.chamis.healthproject.domain.workspace.model.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface WorkspaceJpaRepository extends JpaRepository<Workspace, Long> {
}
