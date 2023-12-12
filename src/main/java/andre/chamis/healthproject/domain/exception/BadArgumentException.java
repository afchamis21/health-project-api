package andre.chamis.healthproject.domain.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception indicating a bad request with a status code of 400 (Bad Request).
 * Extends {@link ExceptionWithStatusCode}.
 */
public class BadArgumentException extends ExceptionWithStatusCode {
    private static final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    /**
     * Default constructor for a BadArgumentException without a specific message.
     * Sets the HTTP status code to 400 (Bad Request).
     */
    public BadArgumentException() {
        super(httpStatus);
    }

    /**
     * Constructor for a BadArgumentException with a specific error message.
     *
     * @param message A detailed error message describing the nature of the bad request.
     *                Sets the HTTP status code to 400 (Bad Request).
     */
    public BadArgumentException(String message) {
        super(message, httpStatus);
    }
}
