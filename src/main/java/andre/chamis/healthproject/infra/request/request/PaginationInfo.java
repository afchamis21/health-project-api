package andre.chamis.healthproject.infra.request.request;


import lombok.Data;

/**
 * Represents pagination information for querying data.
 */
@Data
public class PaginationInfo {
    /**
     * The page number (zero-based) of the pagination.
     */
    private int page = 0;

    /**
     * The size of each page in the pagination.
     */
    private int size = 10;

    /**
     * The sorting mode for the pagination.
     */
    private SortingMode sort = SortingMode.DESC;
}
