package andre.chamis.healthproject.domain.exception;

import andre.chamis.healthproject.context.ServiceContext;
import andre.chamis.healthproject.domain.response.ErrorMessage;
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
     * Constructs an exception with a specific error message, HTTP status, and adds the message to the service context.
     *
     * @param errorMessage The error message associated with the exception.
     * @param httpStatus   The HTTP status associated with the exception.
     */
    public ExceptionWithStatusCode(ErrorMessage errorMessage, HttpStatus httpStatus) {
        super(errorMessage.getMessage());
        this.httpStatus = httpStatus;
        ServiceContext.addMessage(errorMessage.getMessage());
    }
}
