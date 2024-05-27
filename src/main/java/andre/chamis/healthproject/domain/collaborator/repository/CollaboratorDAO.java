package andre.chamis.healthproject.domain.collaborator.repository;

import andre.chamis.healthproject.dao.PaginatedDAO;
import andre.chamis.healthproject.domain.collaborator.dto.GetCollaboratorDTO;
import andre.chamis.healthproject.domain.request.PaginationInfo;
import andre.chamis.healthproject.domain.response.PaginatedResponse;
import andre.chamis.healthproject.domain.user.dto.GetUserDTO;
import andre.chamis.healthproject.domain.user.dto.GetUsernameAndIdDTO;
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
        return "p.create_dt";
    }

    protected String getSelectCollaboratorsByPatientIdQuery() {
        return """
                SELECT p.is_active as is_collaborator_active, p.patient_id as patient_id,
                p.create_dt as collaborator_create_dt, u.user_id, u.email, u.username,
                u.is_registration_complete, u.is_payment_active, u.stripe_client_id, u.is_clocked_in, u.clocked_in_at
                FROM users u
                    JOIN collaborators c ON c.user_id = u.user_id
                    WHERE c.patientId = :patientId
                        AND wu.create_dt <= :now
                    ORDER BY wu.is_active DESC
                """;
    }

    protected String getCountCollaboratorsByPatientIdQuery() {
        return """
                SELECT COUNT(user_id) FROM patients WHERE patient_id = :patientId AND create_dt <= :now
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

    public List<GetUsernameAndIdDTO> getAllCollaboratorNamesByPatientId(Long patientId) {
        String query = """
                SELECT u.username, u.user_id
                FROM collaborator c
                    JOIN users u ON wu.user_id = u.user_id
                    JOIN patients p ON wu.patient_id = p.patient_id
                    WHERE wu.patient_id = :patientId AND u.user_id != p.owner_id;
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
