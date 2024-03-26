package andre.chamis.healthproject.dao;

import andre.chamis.healthproject.domain.request.PaginationInfo;
import andre.chamis.healthproject.domain.response.PaginatedResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Map;

/**
 * Abstract class for performing paginated data access operations.
 *
 * @param <T> The type of data to be accessed.
 */
@Slf4j
public abstract class PaginatedDAO<T> {

    /**
     * Retrieves the NamedParameterJdbcTemplate instance.
     *
     * @return The NamedParameterJdbcTemplate instance.
     */
    protected abstract NamedParameterJdbcTemplate getJdbcTemplate();

    /**
     * Retrieves the name of the column to be used for sorting.
     *
     * @return The name of the sort column.
     */
    protected abstract String getSortColumnName();

    /**
     * Retrieves the ResultSetExtractor for mapping result set rows to objects.
     *
     * @return The ResultSetExtractor for mapping result set rows to objects.
     */
    protected abstract ResultSetExtractor<List<T>> getListResultSetExtractor();

    /**
     * Builds a paginated SQL query based on provided parameters and pagination information.
     *
     * @param params         The parameters for the query.
     * @param paginationInfo The pagination information.
     * @return The paginated SQL query.
     */
    protected String buildPaginatedQuery(Map<String, Object> params, PaginationInfo paginationInfo) {
        String query = "ORDER BY :sortColumn :sortMode LIMIT :size OFFSET :page"
                .replace(":sortColumn", getSortColumnName())
                .replace(":sortMode", paginationInfo.getSort().getValue());

        params.put("size", paginationInfo.getSize());
        params.put("page", paginationInfo.getPage() * paginationInfo.getSize());

        return query;
    }

    /**
     * Retrieves paginated data from the database.
     *
     * @param params         The parameters for the query.
     * @param paginationInfo The pagination information.
     * @param query          The SQL query to execute.
     * @return The list of paginated data.
     */
    protected List<T> getData(Map<String, Object> params, PaginationInfo paginationInfo, String query) {
        query += buildPaginatedQuery(params, paginationInfo);

        log.debug("Running query [{}] with params [{}]", query, params);
        return getJdbcTemplate().query(query, params, getListResultSetExtractor());
    }

    /**
     * Retrieves the total count of records matching the query criteria.
     *
     * @param params The parameters for the query.
     * @param query  The SQL query to execute.
     * @return The total count of records.
     */
    protected Integer getCount(Map<String, Object> params, String query) {
        return getJdbcTemplate().queryForObject(query, params, Integer.class);
    }

    /**
     * Builds a paginated response object.
     *
     * @param data           The paginated data.
     * @param count          The total count of records.
     * @param paginationInfo The pagination information.
     * @return The paginated response object.
     */
    protected PaginatedResponse<T> buildResponse(List<T> data, Integer count, PaginationInfo paginationInfo) {
        if (count == null) {
            return new PaginatedResponse<>(0, data);
        }

        double lastPage = Math.ceil(count / (float) paginationInfo.getSize()) - 1;

        return new PaginatedResponse<>((int) lastPage, data);
    }

    /**
     * Executes a paginated query and builds the response object.
     *
     * @param params         The parameters for the query.
     * @param paginationInfo The pagination information.
     * @param dataQuery      The SQL query for fetching data.
     * @param countQuery     The SQL query for counting records.
     * @return The paginated response object.
     */
    protected PaginatedResponse<T> execute(Map<String, Object> params, PaginationInfo paginationInfo, String dataQuery, String countQuery) {
        List<T> data = getData(params, paginationInfo, dataQuery);
        Integer count = getCount(params, countQuery);
        return buildResponse(data, count, paginationInfo);
    }
}
