package andre.chamis.healthproject.domain.health.collaborator.repository;

import andre.chamis.healthproject.dao.PaginatedDAO;
import andre.chamis.healthproject.domain.health.collaborator.dto.GetCollaboratorDTO;
import andre.chamis.healthproject.domain.user.dto.GetUserSummaryDTO;
import andre.chamis.healthproject.domain.user.dto.GetUsernameAndIdDTO;
import andre.chamis.healthproject.infra.request.request.PaginationInfo;
import andre.chamis.healthproject.infra.request.response.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;

/**
 * Data Access Object (DAO) for collaborator-related operations with pagination support.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
class CollaboratorDAO extends PaginatedDAO<GetCollaboratorDTO> {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PaginatedResponse<GetCollaboratorDTO> getAllCollaboratorsByPatientId(Long patientId, PaginationInfo paginationInfo) {
        Date now = Date.from(Instant.now());
        Map<String, Object> params = new HashMap<>();
        params.put("patientId", patientId);
        params.put("now", now);

        return super.execute(params, paginationInfo, getSelectCollaboratorsByPatientIdQuery(), getCountCollaboratorsByPatientIdQuery());
    }

    @Override
    protected NamedParameterJdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    @Override
    protected String getSortColumnName() {
        return "c.is_active";
    }

    protected String getSelectCollaboratorsByPatientIdQuery() {
        return """
                SELECT c.is_active as is_collaborator_active, c.patient_id as patient_id,
                       c.create_dt as collaborator_create_dt, u.user_id, u.email, u.username
                FROM users u
                         JOIN collaborator c ON c.user_id = u.user_id
                WHERE c.patient_id = :patientId
                  AND c.create_dt <= :now
                """;
    }

    protected String getCountCollaboratorsByPatientIdQuery() {
        return """
                SELECT COUNT(c.user_id) FROM collaborator c WHERE c.patient_id = :patientId AND c.create_dt <= :now
                """;
    }

    @Override
    protected ResultSetExtractor<List<GetCollaboratorDTO>> getListResultSetExtractor() {
        return (rs) -> {
            List<GetCollaboratorDTO> results = new ArrayList<>();
            while (rs.next()) {
                results.add(new GetCollaboratorDTO(
                        rs.getLong("patient_id"),
                        rs.getBoolean("is_collaborator_active"),
                        rs.getTimestamp("collaborator_create_dt"),
                        new GetUserSummaryDTO(
                                rs.getLong("user_id"),
                                rs.getString("username"),
                                rs.getString("email")
                        )
                ));
            }
            return results;
        };
    }

    public List<GetUsernameAndIdDTO> getAllCollaboratorNamesByPatientId(Long patientId) {
        String query = """
                SELECT u.username, u.user_id
                FROM collaborator c
                    JOIN users u ON c.user_id = u.user_id
                    JOIN patients p ON c.patient_id = p.patient_id
                    WHERE c.patient_id = :patientId AND u.user_id != p.owner_id;
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("patientId", patientId);

        return jdbcTemplate.query(query, params, (rs) -> {
            List<GetUsernameAndIdDTO> results = new ArrayList<>();
            while (rs.next()) {
                results.add(new GetUsernameAndIdDTO(
                        rs.getString(1),
                        rs.getLong(2)
                ));
            }
            return results;
        });
    }
}
