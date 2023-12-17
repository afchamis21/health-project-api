package andre.chamis.healthproject.domain.exception;

import andre.chamis.healthproject.domain.response.ErrorMessage;
import org.springframework.http.HttpStatus;

/**
 * Custom exception representing an unauthorized (401) status code.
 */
public class UnauthorizedException extends ExceptionWithStatusCode {
    private static final HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;

    /**
     * Constructs an unauthorized exception with the default HTTP status (401 - Unauthorized).
     */
    public UnauthorizedException() {
        super(httpStatus);
    }

    /**
     * Constructs an unauthorized exception with the given error message and default HTTP status (401 - Unauthorized).
     *
     * @param errorMessage The error message associated with the exception.
     */
    public UnauthorizedException(ErrorMessage errorMessage) {
        super(errorMessage, httpStatus);
    }
}

