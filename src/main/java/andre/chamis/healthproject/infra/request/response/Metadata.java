package andre.chamis.healthproject.infra.request.response;

import lombok.Data;

import java.util.List;

/**
 * Represents metadata associated with a response, typically containing messages.
 */
@Data
public class Metadata {

    /**
     * A set of messages included in the metadata.
     */
    private List<String> messages;
}
