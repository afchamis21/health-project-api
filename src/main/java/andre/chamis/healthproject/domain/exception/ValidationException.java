package andre.chamis.healthproject.domain.exception;

import andre.chamis.healthproject.domain.response.ErrorMessage;
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
