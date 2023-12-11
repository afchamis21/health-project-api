package andre.chamis.healthproject.domain.exception;

import org.springframework.http.HttpStatus;

/**
 * Custom exception indicating a forbidden operation.
 */
public class ForbiddenException extends ExceptionWithStatusCode{
    private static final HttpStatus httpStatus = HttpStatus.FORBIDDEN;

    /**
     * Constructs a forbidden exception with the default HTTP status.
     */
    public ForbiddenException() {
        super(httpStatus);
    }

    /**
     * Constructs a forbidden exception with the given message and default HTTP status.
     *
     * @param message The exception message.
     */
    public ForbiddenException(String message) {
        super(message, httpStatus);
    }
}
