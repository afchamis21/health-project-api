package andre.chamis.healthproject.domain.workspace.member.repository;

import andre.chamis.healthproject.dao.PaginatedDAO;
import andre.chamis.healthproject.domain.request.PaginationInfo;
import andre.chamis.healthproject.domain.response.PaginatedResponse;
import andre.chamis.healthproject.domain.user.dto.GetUserDTO;
import andre.chamis.healthproject.domain.workspace.member.dto.GetWorkspaceMemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;

/**
 * Data Access Object (DAO) for workspace member-related operations with pagination support.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
class WorkspaceMemberDAO extends PaginatedDAO<GetWorkspaceMemberDTO> {
    private final NamedParameterJdbcTemplate jdbcTemplate;


    /**
     * Retrieves all members of a workspace.
     *
     * @param workspaceId    The ID of the workspace.
     * @param paginationInfo The pagination information.
     * @return A paginated response containing the members of the workspace.
     */
    public PaginatedResponse<GetWorkspaceMemberDTO> getAllMembersByWorkspaceId(Long workspaceId, PaginationInfo paginationInfo) {
        Date now = Date.from(Instant.now());
        Map<String, Object> params = new HashMap<>();
        params.put("workspaceId", workspaceId);
        params.put("now", now);

        return super.execute(params, paginationInfo, getSelectMembersByWorkspaceIdQuery(), getCountMembersByWorkspaceIdQuery());
    }

    @Override
    protected NamedParameterJdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    @Override
    protected String getSortColumnName() {
        return "wu.create_dt";
    }

    /**
     * Retrieves the SQL query for selecting members by workspace ID.
     *
     * @return The SQL query for selecting members by workspace ID.
     */
    protected String getSelectMembersByWorkspaceIdQuery() {
        return """
                SELECT wu.is_active as is_member_active, wu.workspace_id as workspace_id,
                wu.create_dt as member_create_dt, u.user_id, u.email, u.username,
                u.is_registration_complete, u.is_payment_active, u.stripe_client_id, u.is_clocked_in, u.clocked_in_at
                FROM users u
                    JOIN workspace_user wu ON wu.user_id = u.user_id
                    WHERE wu.workspace_id = :workspaceId
                        AND wu.create_dt <= :now
                    ORDER BY wu.is_active DESC
                """;
    }

    /**
     * Retrieves the SQL query for counting members by workspace ID.
     *
     * @return The SQL query for counting members by workspace ID.
     */
    protected String getCountMembersByWorkspaceIdQuery() {
        return """
                SELECT COUNT(user_id) FROM workspace_user WHERE workspace_id = :workspaceId AND create_dt <= :now
                """;
    }

    @Override
    protected ResultSetExtractor<List<GetWorkspaceMemberDTO>> getListResultSetExtractor() {
        return (rs) -> {
            List<GetWorkspaceMemberDTO> results = new ArrayList<>();
            while (rs.next()) {
                results.add(new GetWorkspaceMemberDTO(
                        rs.getLong("workspace_id"),
                        rs.getBoolean("is_member_active"),
                        rs.getTimestamp("member_create_dt"),
                        new GetUserDTO(
                                rs.getLong("user_id"),
                                rs.getString("username"),
                                rs.getString("email"),
                                rs.getBoolean("is_registration_complete"),
                                rs.getBoolean("is_payment_active"),
                                null != rs.getString("stripe_client_id"),
                                rs.getBoolean("is_clocked_in"),
                                rs.getLong("clocked_in_at")
                        )
                ));
            }
            return results;
        };
    }
}
