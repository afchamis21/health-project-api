package andre.chamis.healthproject.exception;

import andre.chamis.healthproject.infra.request.response.ErrorMessage;
import lombok.Getter;

/**
 * Custom exception class for validation errors.
 */
@Getter
public class ValidationException extends Exception {
    private final ErrorMessage error;

    public ValidationException(ErrorMessage message) {
        super(message.getMessage());
        error = message;
    }
}
