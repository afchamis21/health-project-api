package andre.chamis.healthproject.controller;


import andre.chamis.healthproject.context.ServiceContext;
import andre.chamis.healthproject.domain.exception.ExceptionWithStatusCode;
import andre.chamis.healthproject.domain.response.ResponseMessage;
import andre.chamis.healthproject.domain.response.ResponseMessageBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global error handling class to manage exceptions and provide consistent responses.
 */
@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    /**
     * Handles exceptions that derive from ExceptionWithStatusCode class.
     *
     * @param ex The exception to be handled.
     * @return ResponseEntity containing the response message and appropriate status code.
     */
    @ExceptionHandler(ExceptionWithStatusCode.class)
    public ResponseEntity<ResponseMessage<Void>> handeExceptionWithStatusCode(ExceptionWithStatusCode ex){
        ServiceContext.addException(ex);
        return ResponseMessageBuilder.build(ex);
    }


    /**
     * Handles generic exceptions that are not specifically caught by other handlers.
     *
     * @param ex The exception to be handled.
     * @return ResponseEntity containing the response message and an internal server error status.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseMessage<Void>> handleGenericException(Exception ex){
        ServiceContext.addException(ex);
        return ResponseMessageBuilder.build(ex);
    }
}
