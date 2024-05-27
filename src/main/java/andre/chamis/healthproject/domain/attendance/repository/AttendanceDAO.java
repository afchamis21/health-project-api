package andre.chamis.healthproject.domain.attendance.repository;

import andre.chamis.healthproject.dao.PaginatedDAO;
import andre.chamis.healthproject.domain.attendance.dto.GetAttendanceWithUsernameDTO;
import andre.chamis.healthproject.domain.request.PaginationInfo;
import andre.chamis.healthproject.domain.response.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class AttendanceDAO extends PaginatedDAO<GetAttendanceWithUsernameDTO> {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public PaginatedResponse<GetAttendanceWithUsernameDTO> searchAllByPatientId(Long patientId, PaginationInfo paginationInfo) {
        Date now = Date.from(Instant.now());
        Map<String, Object> params = new HashMap<>();
        params.put("patientId", patientId);
        params.put("now", now);

        return super.execute(params, paginationInfo, selectAllByPatientIdQuery(), selectCountByPatientIdQuery());
    }

    public PaginatedResponse<GetAttendanceWithUsernameDTO> searchAllBysearchAllByPatientIdAndUsername(Long patientId, Long userId, PaginationInfo paginationInfo) {
        Date now = Date.from(Instant.now());
        Map<String, Object> params = new HashMap<>();
        params.put("patientId", patientId);
        params.put("userId", userId);
        params.put("now", now);

        return super.execute(params, paginationInfo, selectAllByPatientIdAndUsernameQuery(), selectCountByPatientIdAndUsernameQuery());
    }

    private String selectAllByPatientIdQuery() {
        return """
                SELECT wa.*, u.username FROM attendance wa
                    JOIN users u ON wa.user_id = u.user_id
                WHERE wa.patient_id = :patientId
                    AND wa.clock_in_time <= :now
                    ORDER BY wa.clock_in_time DESC
                """;
    }

    private String selectCountByPatientIdQuery() {
        return """
                SELECT COUNT(id) FROM attendance wa JOIN users u ON wa.user_id = u.user_id
                WHERE wa.patient_id = :patientId
                    AND wa.clock_in_time <= :now
                """;
    }

    private String selectAllByPatientIdAndUsernameQuery() {
        return """
                SELECT wa.*, u.username FROM attendance wa
                    JOIN users u ON wa.user_id = u.user_id
                WHERE wa.patient_id = :patientId
                    AND u.user_id = :userId
                    AND wa.clock_in_time <= :now
                    ORDER BY wa.clock_in_time DESC
                """;
    }

    private String selectCountByPatientIdAndUsernameQuery() {
        return """
                SELECT COUNT(id) FROM attendance wa JOIN users u ON wa.user_id = u.user_id
                WHERE wa.patient_id = :patientId
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
                        rs.getLong("patient_id"),
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
