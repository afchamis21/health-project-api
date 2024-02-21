package andre.chamis.healthproject.dao;

import andre.chamis.healthproject.domain.request.PaginationInfo;
import andre.chamis.healthproject.domain.response.PaginatedResponse;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Map;

public abstract class PaginatedDAO<T> {
    protected abstract NamedParameterJdbcTemplate getJdbcTemplate();

    protected abstract String getSortColumnName();

    protected abstract String getDataQuery();

    protected abstract String getCountQuery();

    protected abstract ResultSetExtractor<List<T>> getListResultSetExtractor();

    protected String buildPaginatedQuery(Map<String, Object> params, PaginationInfo paginationInfo) {
        String query = "ORDER BY :sortColumn :sortMode LIMIT :size OFFSET :page"
                .replace(":sortColumn", getSortColumnName());

        params.put(":sortMode", paginationInfo.getSort().getValue());
        params.put("size", paginationInfo.getSize());
        params.put("page", paginationInfo.getPage() * paginationInfo.getSize());

        return query;
    }

    protected List<T> getData(Map<String, Object> params, PaginationInfo paginationInfo) {
        String query = getDataQuery();

        query += buildPaginatedQuery(params, paginationInfo);

        return getJdbcTemplate().query(query, params, getListResultSetExtractor());
    }

    protected Integer getCount(Map<String, Object> params) {
        String query = getCountQuery();
        return getJdbcTemplate().queryForObject(query, params, Integer.class);
    }

    protected PaginatedResponse<T> buildResponse(List<T> data, Integer count, PaginationInfo paginationInfo) {
        if (count == null) {
            return new PaginatedResponse<>(0, data);
        }

        double lastPage = Math.ceil(count / (float) paginationInfo.getSize()) - 1;

        return new PaginatedResponse<>((int) lastPage, data);
    }

    protected PaginatedResponse<T> execute(Map<String, Object> params, PaginationInfo paginationInfo) {
        List<T> data = getData(params, paginationInfo);
        Integer count = getCount(params);
        return buildResponse(data, count, paginationInfo);
    }
}
