package andre.chamis.healthproject.domain.exception;

import andre.chamis.healthproject.context.ServiceContext;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Abstract base class for custom exceptions with associated HTTP status codes.
 */
@Getter
public abstract class ExceptionWithStatusCode extends RuntimeException {
    /**
     * The HTTP status code associated with the exception.
     */
    protected HttpStatus httpStatus;

    /**
     * Constructs an exception with the given HTTP status.
     *
     * @param httpStatus The HTTP status associated with the exception.
     */
    public ExceptionWithStatusCode(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    /**
     * Constructs an exception with the given message and HTTP status.
     *
     * @param message    The exception message.
     * @param httpStatus The HTTP status associated with the exception.
     */
    public ExceptionWithStatusCode(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
        ServiceContext.addMessage(message);
    }
}
