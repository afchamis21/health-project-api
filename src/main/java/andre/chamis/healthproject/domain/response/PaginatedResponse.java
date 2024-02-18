package andre.chamis.healthproject.domain.response;

import java.util.List;

public record PaginatedResponse<T>(
        int lastPage, List<T> data
) {

}
