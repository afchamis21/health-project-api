package andre.chamis.healthproject.domain.workspace.repository;

import andre.chamis.healthproject.domain.request.PaginationInfo;
import andre.chamis.healthproject.domain.response.PaginatedResponse;
import andre.chamis.healthproject.domain.workspace.dto.GetWorkspaceDTO;
import andre.chamis.healthproject.domain.workspace.model.Workspace;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Date;
import java.util.List;
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
     * Retrieves a paginated list of workspaces that a user is a member of.
     *
     * @param userId         The ID of the user who is a member of the workspaces.
     * @param paginationInfo The pagination information.
     * @return A paginated response containing the list of workspaces.
     */
    public PaginatedResponse<GetWorkspaceDTO> findWorkspacesByMemberId(Long userId, PaginationInfo paginationInfo) {
        return workspaceDAO.getWorkspacesByMemberId(userId, paginationInfo);
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

    public boolean existsWorkspaceByWorkspaceIdAndOwnerId(Long workspaceId, Long ownerId) {
        return jpaRepository.existsWorkspaceByWorkspaceIdAndOwnerId(workspaceId, ownerId);
    }

    public List<GetWorkspaceDTO> getWorkspaceDtoByIdIfOwnerOrMemberAndActive(Long workspaceId, Long userId) {
        return jpaRepository.getWorkspaceDtoByIdIfOwnerOrMemberAndActive(workspaceId, userId);
    }
}
