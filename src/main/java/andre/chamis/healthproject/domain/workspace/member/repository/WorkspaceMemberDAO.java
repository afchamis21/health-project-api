package andre.chamis.healthproject.domain.workspace.member.repository;

import andre.chamis.healthproject.domain.user.dto.GetUserDTO;
import andre.chamis.healthproject.domain.workspace.member.dto.GetWorkspaceMemberDTO;
import andre.chamis.healthproject.domain.workspace.member.dto.GetWorkspaceMembersDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;

@Slf4j
@Repository
@RequiredArgsConstructor
class WorkspaceMemberDAO {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public GetWorkspaceMembersDTO getAllMembersByWorkspaceId(Long workspaceId, int page, int size) {
        Date now = Date.from(Instant.now());
        String query = """
                SELECT wu.is_active as is_member_active, wu.create_dt as member_create_dt, u.user_id, u.email, u.username, u.is_registration_complete, u.is_payment_active, u.stripe_client_id FROM users u
                    JOIN workspace_user wu ON wu.user_id = u.user_id
                    WHERE wu.workspace_id = :workspaceId
                        AND wu.create_dt <= :now
                    LIMIT :size OFFSET :page
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("workspaceId", workspaceId);
        params.put("size", size);
        params.put("page", page * size);
        params.put("now", now);

        List<GetWorkspaceMemberDTO> users = jdbcTemplate.query(query, params, (rs) -> {
            List<GetWorkspaceMemberDTO> results = new ArrayList<>();
            while (rs.next()) {
                results.add(new GetWorkspaceMemberDTO(
                        workspaceId,
                        rs.getBoolean("is_member_active"),
                        rs.getDate("member_create_dt"),
                        new GetUserDTO(
                                rs.getLong("user_id"),
                                rs.getString("username"),
                                rs.getString("email"),
                                rs.getBoolean("is_registration_complete"),
                                rs.getBoolean("is_payment_active"),
                                null != rs.getString("stripe_client_id")
                        )
                ));
            }
            return results;
        });

        String countQuery = """
                SELECT COUNT(user_id) FROM workspace_user WHERE workspace_id = :workspaceId AND create_dt <= :now
                """;

        Map<String, Object> countQueryParams = new HashMap<>();
        countQueryParams.put("workspaceId", workspaceId);
        countQueryParams.put("now", now);

        Integer totalMembers = jdbcTemplate.queryForObject(countQuery, countQueryParams, Integer.class);

        if (totalMembers == null) {
            return new GetWorkspaceMembersDTO(0, users);
        }

        double lastPage = Math.ceil(totalMembers / (float) size) - 1;

        return new GetWorkspaceMembersDTO((int) lastPage, users);
    }
}
