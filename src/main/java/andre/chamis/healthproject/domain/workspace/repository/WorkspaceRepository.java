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

/**
 * Repository class for managing workspace entities using both JPA and custom data access objects.
 */
@Repository
@RequiredArgsConstructor
public class WorkspaceRepository {
    private final WorkspaceDAO workspaceDAO;
    private final WorkspaceJpaRepository jpaRepository;

    /**
     * Saves a workspace, updating both the database and the entity.
     * The update date of the workspace is set to the current date and time.
     *
     * @param workspace The workspace to be saved.
     * @return The saved workspace.
     */
    public Workspace save(Workspace workspace) {
        workspace.setUpdateDt(Date.from(Instant.now()));
        return jpaRepository.save(workspace);
    }

    /**
     * Finds a workspace by its ID.
     *
     * @param workspaceId The ID of the workspace to find.
     * @return An {@link Optional} containing the found workspace, or empty if not found.
     */
    public Optional<Workspace> findByWorkspaceId(Long workspaceId) {
        return jpaRepository.findById(workspaceId);
    }

    /**
     * Deletes a workspace by its ID.
     *
     * @param workspaceId The ID of the workspace to delete.
     */
    public void deleteByWorkspaceId(Long workspaceId) {
        jpaRepository.deleteById(workspaceId);
    }

    /**
     * Retrieves a paginated list of workspaces owned by a user.
     *
     * @param userId         The ID of the user who owns the workspaces.
     * @param paginationInfo The pagination information.
     * @return A paginated response containing the list of workspaces.
     */
    public PaginatedResponse<GetWorkspaceDTO> findWorkspacesByOwnerId(Long userId, PaginationInfo paginationInfo) {
        return workspaceDAO.getWorkspacesByOwnerId(userId, paginationInfo);
    }

    /**
     * Searches for workspaces by name and member ID.
     *
     * @param userId         The ID of the user who is a member of the workspaces.
     * @param name           The name to search for within the workspace names.
     * @param paginationInfo The pagination information.
     * @return A paginated response containing the list of matching workspaces.
     */
    public PaginatedResponse<GetWorkspaceDTO> searchWorkspacesByNameAndMemberId(Long userId, String name, PaginationInfo paginationInfo) {
        return workspaceDAO.searchWorkspacesByNameAndMemberId(userId, name, paginationInfo);
    }
}
