package andre.chamis.healthproject.domain.health.patient.repository;

import andre.chamis.healthproject.dao.PaginatedDAO;
import andre.chamis.healthproject.domain.health.patient.dto.GetPatientSummaryDTO;
import andre.chamis.healthproject.infra.request.request.PaginationInfo;
import andre.chamis.healthproject.infra.request.response.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;


@Repository
@RequiredArgsConstructor
class PatientDAO extends PaginatedDAO<GetPatientSummaryDTO> {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PaginatedResponse<GetPatientSummaryDTO> getPatientsByCollaboratorId(Long collaboratorId, PaginationInfo paginationInfo) {
        Date now = Date.from(Instant.now());
        Map<String, Object> params = new HashMap<>();
        params.put("userId", collaboratorId);
        params.put("now", now);

        return super.execute(params, paginationInfo, getSearchByCollaboratorIdQuery(), getCountByCollaboratorIdQuery());
    }

    public PaginatedResponse<GetPatientSummaryDTO> searchPatientsByNameAndCollaboratorId(Long collaboratorId, String name, PaginationInfo paginationInfo) {
        Date now = Date.from(Instant.now());
        Map<String, Object> params = new HashMap<>();
        params.put("userId", collaboratorId);
        params.put("name", name);
        params.put("now", now);

        return super.execute(params, paginationInfo, getSearchByNameAndCollaboratorIdQuery(), getCountByPatientNameAndCollaboratorIdQuery());
    }


    private String getSearchByNameAndCollaboratorIdQuery() {
        return """
                SELECT DISTINCT p.patient_id, p.owner_id, p.create_dt, p.is_active, p.update_dt, p.patient_id, CONCAT(p.name, COALESCE(' ' || p.surname, '')) AS full_name
                FROM patients p
                    JOIN collaborator wu ON p.patient_id = wu.patient_id
                WHERE ((p.owner_id = :userId AND wu.user_id = :userId) OR (wu.user_id = :userId AND wu.is_active = true AND p.is_active = true))
                  AND p.create_dt <= :now
                  AND CONCAT(p.name, COALESCE(' ' || p.surname, '')) ILIKE :name || '%'
                """;
    }

    private String getCountByPatientNameAndCollaboratorIdQuery() {
        return """
                SELECT COUNT(p.patient_id)
                FROM patients p
                    JOIN collaborator wu ON p.patient_id = wu.patient_id
                WHERE ((p.owner_id = :userId AND wu.user_id = :userId) OR (wu.user_id = :userId AND wu.is_active = true AND p.is_active = true))
                  AND CONCAT(p.name, COALESCE(' ' || p.surname, '')) ILIKE :name || '%'
                  AND p.create_dt <= :now
                """;
    }

    private String getSearchByCollaboratorIdQuery() {
        return """
                SELECT DISTINCT p.patient_id, p.owner_id, p.create_dt, p.is_active, p.update_dt, p.patient_id, CONCAT(p.name, COALESCE(' ' || p.surname, '')) AS full_name
                FROM patients p
                    JOIN collaborator wu ON p.patient_id = wu.patient_id
                WHERE ((p.owner_id = :userId AND wu.user_id = :userId) OR (wu.user_id = :userId AND wu.is_active = true AND p.is_active = true))
                  AND p.create_dt <= :now
                """;
    }

    private String getCountByCollaboratorIdQuery() {
        return """
                SELECT COUNT(DISTINCT p.patient_id)
                FROM patients p
                    JOIN collaborator wu ON p.patient_id = wu.patient_id
                WHERE ((p.owner_id = :userId AND wu.user_id = :userId) OR (wu.user_id = :userId AND wu.is_active = true AND p.is_active = true))
                  AND p.create_dt <= :now
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
    protected ResultSetExtractor<List<GetPatientSummaryDTO>> getListResultSetExtractor() {
        return (rs) -> {
            List<GetPatientSummaryDTO> results = new ArrayList<>();
            while (rs.next()) {
                results.add(new GetPatientSummaryDTO(
                        rs.getLong("patient_id"),
                        rs.getString("full_name"),
                        rs.getLong("owner_id"),
                        rs.getBoolean("is_active"),
                        rs.getDate("create_dt")
                ));
            }
            return results;
        };
    }
}
