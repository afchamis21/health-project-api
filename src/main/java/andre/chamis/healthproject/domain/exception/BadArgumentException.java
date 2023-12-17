package andre.chamis.healthproject.domain.exception;

import andre.chamis.healthproject.domain.response.ErrorMessage;
import org.springframework.http.HttpStatus;

/**
 * Exception indicating a bad request with a status code of 400 (Bad Request).
 * Extends {@link ExceptionWithStatusCode}.
 */
public class BadArgumentException extends ExceptionWithStatusCode {
    private static final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    /**
     * Constructor for a BadArgumentException with a specific error message.
     *
     * @param errorMessage A detailed error message describing the nature of the bad request.
     */
    public BadArgumentException(ErrorMessage errorMessage) {
        super(errorMessage, httpStatus);
    }
}
