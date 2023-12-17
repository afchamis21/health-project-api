package andre.chamis.healthproject.domain.exception;

import andre.chamis.healthproject.domain.response.ErrorMessage;
import org.springframework.http.HttpStatus;

/**
 * Custom exception representing a forbidden (403) status code.
 */
public class ForbiddenException extends ExceptionWithStatusCode {
    private static final HttpStatus httpStatus = HttpStatus.FORBIDDEN;

    /**
     * Constructs a forbidden exception with the default HTTP status (403 - Forbidden).
     */
    public ForbiddenException() {
        super(httpStatus);
    }

    /**
     * Constructs a forbidden exception with the given error message and default HTTP status (403 - Forbidden).
     *
     * @param errorMessage The error message associated with the exception.
     */
    public ForbiddenException(ErrorMessage errorMessage) {
        super(errorMessage, httpStatus);
    }
}
