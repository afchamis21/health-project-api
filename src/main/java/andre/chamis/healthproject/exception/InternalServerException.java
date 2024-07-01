package andre.chamis.healthproject.exception;

import andre.chamis.healthproject.infra.request.response.ErrorMessage;
import org.springframework.http.HttpStatus;

public class InternalServerException extends ExceptionWithStatusCode {
    public InternalServerException(ErrorMessage errorMessage) {
        super(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
