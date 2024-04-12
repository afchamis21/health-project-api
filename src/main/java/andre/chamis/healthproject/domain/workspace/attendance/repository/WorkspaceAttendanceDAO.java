package andre.chamis.healthproject.domain.workspace.attendance.repository;

import andre.chamis.healthproject.dao.PaginatedDAO;
import andre.chamis.healthproject.domain.request.PaginationInfo;
import andre.chamis.healthproject.domain.response.PaginatedResponse;
import andre.chamis.healthproject.domain.workspace.attendance.dto.GetAttendanceWithUsernameDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class WorkspaceAttendanceDAO extends PaginatedDAO<GetAttendanceWithUsernameDTO> {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public PaginatedResponse<GetAttendanceWithUsernameDTO> searchAllByWorkspaceId(Long workspaceId, PaginationInfo paginationInfo) {
        Date now = Date.from(Instant.now());
        Map<String, Object> params = new HashMap<>();
        params.put("workspaceId", workspaceId);
        params.put("now", now);

        return super.execute(params, paginationInfo, selectAllByWorkspaceIdQuery(), selectCountByWorkspaceIdQuery());
    }

    public PaginatedResponse<GetAttendanceWithUsernameDTO> searchAllByWorkspaceIdAndUsername(Long workspaceId, Long userId, PaginationInfo paginationInfo) {
        Date now = Date.from(Instant.now());
        Map<String, Object> params = new HashMap<>();
        params.put("workspaceId", workspaceId);
        params.put("userId", userId);
        params.put("now", now);

        return super.execute(params, paginationInfo, selectAllByWorkspaceIdAndUsernameQuery(), selectCountByWorkspaceIdAndUsernameQuery());
    }

    private String selectAllByWorkspaceIdQuery() {
        return """
                SELECT wa.*, u.username FROM workspace_attendance wa
                    JOIN users u ON wa.user_id = u.user_id
                WHERE wa.workspace_id = :workspaceId
                    AND wa.clock_in_time <= :now
                    ORDER BY wa.clock_in_time DESC
                """;
    }

    private String selectCountByWorkspaceIdQuery() {
        return """
                SELECT COUNT(id) FROM workspace_attendance wa JOIN users u ON wa.user_id = u.user_id
                WHERE wa.workspace_id = :workspaceId
                    AND wa.clock_in_time <= :now
                """;
    }

    private String selectAllByWorkspaceIdAndUsernameQuery() {
        return """
                SELECT wa.*, u.username FROM workspace_attendance wa
                    JOIN users u ON wa.user_id = u.user_id
                WHERE wa.workspace_id = :workspaceId
                    AND u.user_id = :userId
                    AND wa.clock_in_time <= :now
                    ORDER BY wa.clock_in_time DESC
                """;
    }

    private String selectCountByWorkspaceIdAndUsernameQuery() {
        return """
                SELECT COUNT(id) FROM workspace_attendance wa JOIN users u ON wa.user_id = u.user_id
                WHERE wa.workspace_id = :workspaceId
                    AND u.user_id = :userId
                    AND wa.clock_in_time <= :now
                """;
    }

    @Override
    protected NamedParameterJdbcTemplate getJdbcTemplate() {
        return namedParameterJdbcTemplate;
    }

    @Override
    protected String getSortColumnName() {
        return "clock_in_time";
    }

    @Override
    protected ResultSetExtractor<List<GetAttendanceWithUsernameDTO>> getListResultSetExtractor() {
        return (rs) -> {
            List<GetAttendanceWithUsernameDTO> results = new ArrayList<>();
            while (rs.next()) {
                results.add(new GetAttendanceWithUsernameDTO(
                        rs.getLong("workspace_id"),
                        rs.getLong("user_id"),
                        rs.getTimestamp("clock_in_time"),
                        rs.getTimestamp("clock_out_time"),
                        rs.getString("username")
                ));
            }
            return results;
        };
    }
}
