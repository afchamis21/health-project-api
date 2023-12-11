package andre.chamis.healthproject.domain.exception;

import org.springframework.http.HttpStatus;

/**
 * Custom exception indicating that an entity was not found.
 */
public class EntityNotFoundException extends ExceptionWithStatusCode{
    /**
     * Constructs an entity not found exception with the given HTTP status.
     *
     * @param httpStatus The HTTP status associated with the exception.
     */
    public EntityNotFoundException(HttpStatus httpStatus) {
        super(httpStatus);
    }

    /**
     * Constructs an entity not found exception with the given message and HTTP status.
     *
     * @param message    The exception message.
     * @param httpStatus The HTTP status associated with the exception.
     */
    public EntityNotFoundException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
