package andre.chamis.healthproject.domain.exception;

import org.springframework.http.HttpStatus;

/**
 * Custom exception indicating unauthorized access.
 */
public class UnauthorizedException extends ExceptionWithStatusCode{
    private static final HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;

    /**
     * Constructs an unauthorized exception with the default HTTP status.
     */
    public UnauthorizedException() {
        super(httpStatus);
    }

    /**
     * Constructs an unauthorized exception with the given message and default HTTP status.
     *
     * @param message The exception message.
     */
    public UnauthorizedException(String message) {
        super(message, httpStatus);
    }
}
