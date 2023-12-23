package andre.chamis.healthproject.domain.response;

import lombok.Data;

/**
 * Represents a response message containing both metadata and data.
 *
 * @param <T> The type of the data included in the response.
 */
@Data
public class ResponseMessage<T> {
    /**
     * The metadata associated with the response.
     */
    private Metadata metadata;

    /**
     * The metadata associated with the response.
     */
    private T body;
}
