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

@Repository
@RequiredArgsConstructor
class WorkspaceDAO extends PaginatedDAO<GetWorkspaceDTO> {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PaginatedResponse<GetWorkspaceDTO> getWorkspacesByOwnerId(Long userId, PaginationInfo paginationInfo) {
        Date now = Date.from(Instant.now());
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("now", now);

        return super.execute(params, paginationInfo);
    }

    @Override
    protected NamedParameterJdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    @Override
    protected String getDataQuery() {
        return """
                SELECT workspace_id, workspace_name, owner_id, is_active, create_dt FROM workspaces WHERE owner_id = :userId
                AND create_dt <= :now
                ORDER BY create_dt
                """;
    }

    @Override
    protected String getCountQuery() {
        return """
                SELECT COUNT(workspace_id) FROM workspaces WHERE owner_id = :userId AND create_dt <= :now
                """;
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
