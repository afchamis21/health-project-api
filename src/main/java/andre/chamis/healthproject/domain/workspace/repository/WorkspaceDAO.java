package andre.chamis.healthproject.domain.workspace.repository;

import andre.chamis.healthproject.dao.PaginatedDAO;
import andre.chamis.healthproject.domain.request.PaginationInfo;
import andre.chamis.healthproject.domain.response.PaginatedResponse;
import andre.chamis.healthproject.domain.workspace.dto.GetWorkspaceDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;

/**
 * Data Access Object (DAO) for workspace-related operations with pagination support.
 */
@Repository
@RequiredArgsConstructor
class WorkspaceDAO extends PaginatedDAO<GetWorkspaceDTO> {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * Retrieves workspaces that a user is member.
     *
     * @param userId         The ID of the member user.
     * @param paginationInfo The pagination information.
     * @return A paginated response containing the workspaces owned by the user.
     */
    public PaginatedResponse<GetWorkspaceDTO> getWorkspacesByMemberId(Long userId, PaginationInfo paginationInfo) {
        Date now = Date.from(Instant.now());
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("now", now);

        return super.execute(params, paginationInfo, getSearchByMemberIdQuery(), getCountByMemberIdQuery());
    }

    /**
     * Searches workspaces by name and member ID.
     *
     * @param userId         The ID of the user.
     * @param name           The name of the workspace.
     * @param paginationInfo The pagination information.
     * @return A paginated response containing the workspaces matching the search criteria.
     */
    public PaginatedResponse<GetWorkspaceDTO> searchWorkspacesByNameAndMemberId(Long userId, String name, PaginationInfo paginationInfo) {
        Date now = Date.from(Instant.now());
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("name", name);
        params.put("now", now);

        return super.execute(params, paginationInfo, getSearchByWorkspaceNameAndMemberIdQuery(), getCountByWorkspaceNameAndMemberIdQuery());
    }


    private String getSearchByWorkspaceNameAndMemberIdQuery() {
        return """
                SELECT w.workspace_id, w.owner_id, w.workspace_name,w.create_dt, w.is_active, w.update_dt FROM workspaces w
                    JOIN workspace_user wu ON w.workspace_id = wu.workspace_id
                         WHERE wu.user_id = :userId AND wu.is_active = true
                         AND w.workspace_name ILIKE :name || '%'
                         AND w.create_dt <= :now
                """;
    }

    protected String getCountByWorkspaceNameAndMemberIdQuery() {
        return """
                SELECT COUNT(w.workspace_id) FROM workspaces w
                    JOIN workspace_user wu ON w.workspace_id = wu.workspace_id
                         WHERE wu.user_id = :userId AND wu.is_active = true
                         AND w.workspace_name ILIKE :name || '%'
                         AND w.create_dt <= :now
                """;
    }

    private String getSearchByMemberIdQuery() {
        return """
                SELECT w.workspace_id, w.owner_id, w.workspace_name,w.create_dt, w.is_active, w.update_dt FROM workspaces w
                    JOIN workspace_user wu ON w.workspace_id = wu.workspace_id
                         WHERE wu.user_id = :userId AND wu.is_active = true
                         AND w.create_dt <= :now
                """;
    }

    protected String getCountByMemberIdQuery() {
        return """
                SELECT COUNT(w.workspace_id) FROM workspaces w
                    JOIN workspace_user wu ON w.workspace_id = wu.workspace_id
                         WHERE wu.user_id = :userId AND wu.is_active = true
                         AND w.create_dt <= :now
                """;
    }

    @Override
    protected NamedParameterJdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    @Override
    protected String getSortColumnName() {
        return "create_dt";
    }

    @Override
    protected ResultSetExtractor<List<GetWorkspaceDTO>> getListResultSetExtractor() {
        return (rs) -> {
            List<GetWorkspaceDTO> results = new ArrayList<>();
            while (rs.next()) {
                results.add(new GetWorkspaceDTO(
                        rs.getLong("workspace_id"),
                        rs.getString("workspace_name"),
                        rs.getLong("owner_id"),
                        rs.getBoolean("is_active"),
                        rs.getDate("create_dt")
                ));
            }
            return results;
        };
    }
}
