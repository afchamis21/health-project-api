package andre.chamis.healthproject.domain.response;

import java.util.List;

/**
 * Represents a paginated response containing data and pagination information.
 *
 * @param <T> The type of data in the response.
 */
public record PaginatedResponse<T>(

        int lastPage, List<T> data
) {
}
