package andre.chamis.healthproject.domain.workspace.repository;

import andre.chamis.healthproject.domain.workspace.dto.GetWorkspaceDTO;
import andre.chamis.healthproject.domain.workspace.dto.GetWorkspacesDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;

@Repository
@RequiredArgsConstructor
class WorkspaceDAO {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public GetWorkspacesDTO getWorkspacesByOwnerId(Long userId, int page, int size) {
        Date now = Date.from(Instant.now());
        String query = """
                SELECT workspace_id, workspace_name, owner_id, is_active, create_dt FROM workspaces WHERE owner_id = :userId
                AND create_dt <= :now
                ORDER BY create_dt LIMIT :size OFFSET :page
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("size", size);
        params.put("page", page * size);
        params.put("now", now);

        List<GetWorkspaceDTO> workspaces = jdbcTemplate.query(query, params, (rs) -> {
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
        });

        String countQuery = """
                SELECT COUNT(workspace_id) FROM workspaces WHERE owner_id = :userId AND create_dt <= :now
                """;

        Map<String, Object> countQueryParams = new HashMap<>();
        countQueryParams.put("userId", userId);
        countQueryParams.put("now", now);

        Integer totalMembers = jdbcTemplate.queryForObject(countQuery, countQueryParams, Integer.class);

        if (totalMembers == null) {
            return new GetWorkspacesDTO(0, workspaces);
        }

        double lastPage = Math.ceil(totalMembers / (float) size) - 1;

        return new GetWorkspacesDTO((int) lastPage, workspaces);
    }
}
