package andre.chamis.healthproject.controller;


import andre.chamis.healthproject.context.ServiceContext;
import andre.chamis.healthproject.exception.ExceptionWithStatusCode;
import andre.chamis.healthproject.infra.request.response.ResponseMessage;
import andre.chamis.healthproject.infra.request.response.ResponseMessageBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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
    public ResponseEntity<ResponseMessage<Void>> handeExceptionWithStatusCode(ExceptionWithStatusCode ex) {
        ServiceContext.addException(ex);
        return ResponseMessageBuilder.build(ex);
    }

    /**
     * Handles missing servlet request parameter exceptions.
     *
     * @param ex The exception to be handled.
     * @return ResponseEntity containing the response message and HTTP status code 400 (Bad Request).
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ResponseMessage<Void>> handleMissingRequestParamException(MissingServletRequestParameterException ex) {
        ServiceContext.addException(ex);
        return ResponseMessageBuilder.build(HttpStatus.BAD_REQUEST, "O parâmetro {param} é obrigatório".replace("{param}", ex.getParameterName()));
    }

    /**
     * Handles exceptions when a requested resource is not found.
     *
     * @param ex The exception to be handled.
     * @return ResponseEntity containing the response message and HTTP status code 404 (Not Found).
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ResponseMessage<Void>> handleNoResourceFoundException(NoResourceFoundException ex) {
        ServiceContext.addException(ex);
        return ResponseMessageBuilder.build(HttpStatus.NOT_FOUND);
    }

    /**
     * Handles HTTP message not readable exceptions.
     *
     * @param ex The exception to be handled.
     * @return ResponseEntity containing the response message and HTTP status code 400 (Bad Request).
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseMessage<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        ServiceContext.addException(ex);
        return ResponseMessageBuilder.build(HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles generic exceptions that are not specifically caught by other handlers.
     *
     * @param ex The exception to be handled.
     * @return ResponseEntity containing the response message and an internal server error status.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseMessage<Void>> handleGenericException(Exception ex) {
        ServiceContext.addException(ex);
        return ResponseMessageBuilder.build(ex);
    }
}
