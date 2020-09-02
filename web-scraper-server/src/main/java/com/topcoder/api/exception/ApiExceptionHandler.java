package com.topcoder.api.exception;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The exception handler that maps exceptions to corresponding response status and message.
 */
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {


    /**
     * The message source.
     */
    @Autowired
    private MessageSource messageSource;

    private static final String UNKOWN_ERROR = "Internal server error happened, please try again later";

    /**
     * Build error response.
     *
     * @param status  the http status
     * @param message the message
     * @return the error response entity with code and message
     */
    private static ResponseEntity<Object> buildErrorResponse(HttpStatus status, String message) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", message);
        return new ResponseEntity<>(responseBody, status);
    }


    /**
     * Handle illegal argument exception.
     *
     * @param ex the exception
     * @return the error response entity
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error(ex);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Handle spring entity not found exception.
     *
     * @param ex the exception
     * @return the error response entity
     */
    @ExceptionHandler(javax.persistence.EntityNotFoundException.class)
    @ResponseBody
    public ResponseEntity<Object> handleJavaxEntityNotfound(
            javax.persistence.EntityNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }


    /**
     * Handle invalid method argument type exception.
     *
     * @param ex the exception
     * @return the error response entity
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex) {
        String requiredType = ex.getRequiredType().getSimpleName().toLowerCase();

        if (ex.getCause() instanceof NumberFormatException) {
            requiredType += " number";
        }

        return buildErrorResponse(HttpStatus.BAD_REQUEST,
                String.format("%s must be a %s", ex.getParameter().getParameterName(), requiredType));
    }

    /**
     * Handle application exceptions.
     *
     * @param ex the exception
     * @return the error response entity
     */
    @ExceptionHandler(ApiException.class)
    @ResponseBody
    public ResponseEntity<Object> handleApplicationException(ApiException ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        if (ex instanceof EntityNotFoundException) {
            status = HttpStatus.NOT_FOUND;
        } else if (ex instanceof ConflictException) {
            status = HttpStatus.CONFLICT;
        } else if (ex instanceof BadRequestException) {
            status = HttpStatus.BAD_REQUEST;
        }
        return buildErrorResponse(status, ex.getMessage());
    }


    /**
     * Handle the other exceptions.
     *
     * @param throwable the throwable
     * @return the error response entity
     */
    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public ResponseEntity<Object> handleOtherExceptions(Throwable throwable) {
        if (throwable.getCause() instanceof ConstraintViolationException) {
            ConstraintViolationException cause = (ConstraintViolationException) throwable.getCause();
            String message = cause.getCause().getMessage();
            if (message != null && message.toUpperCase().contains("FOR KEY")) {
                message = message.substring(0, message.indexOf("for key"));
            }
            return buildErrorResponse(HttpStatus.EXPECTATION_FAILED, message);
        } else {
            throwable.printStackTrace();
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, UNKOWN_ERROR);
        }
    }

    /**
     * A single place to customize the response body of all exception types.
     *
     * @param ex      the exception
     * @param body    the body for the response
     * @param headers the headers for the response
     * @param status  the response status
     * @param request the current request
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body,
                                                             HttpHeaders headers, HttpStatus status, WebRequest request) {
        String message = ex.getMessage();

        if (ex instanceof HttpMessageNotReadableException) {
            message = "Request body is missing or invalid, " + ex.getMessage();
        } else if (ex instanceof ConfigurationException) {
            message = ex.getMessage();
        } else if (ex instanceof BindException) {
            message = convertErrorsToMessage(((BindException) ex).getAllErrors());
        } else if (ex instanceof MethodArgumentNotValidException) {
            message = convertErrorsToMessage(
                    ((MethodArgumentNotValidException) ex).getBindingResult().getAllErrors());
        } else if (status == HttpStatus.INTERNAL_SERVER_ERROR) {
            message = UNKOWN_ERROR;
        }
        return buildErrorResponse(status, message);
    }

    /**
     * Convert object errors to error message string.
     *
     * @param objectErrors the list of object errors
     * @return the comma separated error message
     */
    private String convertErrorsToMessage(List<ObjectError> objectErrors) {
        List<String> messages = new ArrayList<>();

        for (ObjectError objectError : objectErrors) {
            messages.add(messageSource.getMessage(objectError, null));
        }

        return StringUtils.collectionToDelimitedString(messages, ", ");
    }
}
