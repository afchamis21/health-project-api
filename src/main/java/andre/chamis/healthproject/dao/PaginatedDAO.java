package andre.chamis.healthproject.dao;

import andre.chamis.healthproject.domain.request.PaginationInfo;
import andre.chamis.healthproject.domain.response.PaginatedResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
public abstract class PaginatedDAO<T> {
    protected abstract NamedParameterJdbcTemplate getJdbcTemplate();

    protected abstract String getSortColumnName();


    protected abstract ResultSetExtractor<List<T>> getListResultSetExtractor();

    protected String buildPaginatedQuery(Map<String, Object> params, PaginationInfo paginationInfo) {
        String query = "ORDER BY :sortColumn :sortMode LIMIT :size OFFSET :page"
                .replace(":sortColumn", getSortColumnName())
                .replace(":sortMode", paginationInfo.getSort().getValue());

        params.put("size", paginationInfo.getSize());
        params.put("page", paginationInfo.getPage() * paginationInfo.getSize());

        return query;
    }

    protected List<T> getData(Map<String, Object> params, PaginationInfo paginationInfo, String query) {
        query += buildPaginatedQuery(params, paginationInfo);

        log.debug("Running query [{}] with params [{}]", query, params);
        return getJdbcTemplate().query(query, params, getListResultSetExtractor());
    }

    protected Integer getCount(Map<String, Object> params, String query) {
        return getJdbcTemplate().queryForObject(query, params, Integer.class);
    }

    protected PaginatedResponse<T> buildResponse(List<T> data, Integer count, PaginationInfo paginationInfo) {
        if (count == null) {
            return new PaginatedResponse<>(0, data);
        }

        double lastPage = Math.ceil(count / (float) paginationInfo.getSize()) - 1;

        return new PaginatedResponse<>((int) lastPage, data);
    }

    protected PaginatedResponse<T> execute(Map<String, Object> params, PaginationInfo paginationInfo, String dataQuery, String countQuery) {
        List<T> data = getData(params, paginationInfo, dataQuery);
        Integer count = getCount(params, countQuery);
        return buildResponse(data, count, paginationInfo);
    }
}
