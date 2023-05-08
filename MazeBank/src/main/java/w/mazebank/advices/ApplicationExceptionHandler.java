package w.mazebank.advices;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import w.mazebank.exceptions.*;
import w.mazebank.utils.ResponseHandler;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApplicationExceptionHandler {
    // handle Not Found Exception
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(NotFoundException e) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", e.getMessage());
        return ResponseHandler.generateErrorResponse(errors, HttpStatus.NOT_FOUND, "Not Found");
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException e) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", e.getMessage());
        return ResponseHandler.generateErrorResponse(errors, HttpStatus.FORBIDDEN, "Access Denied");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleInvalidArgument(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseHandler.generateErrorResponse(errors, HttpStatus.BAD_REQUEST, "Validation Error");
    }

    // handle invalid types and enums
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleJsonErrors(HttpMessageNotReadableException e) {
        Map<String, String> errors = new HashMap<>();
        MismatchedInputException cause = (MismatchedInputException) e.getCause();
        if (e.getCause() instanceof InvalidFormatException &&
            cause.getTargetType().getPackageName().contains("enums")) {
            errors.put(cause.getPath().get(0).getFieldName(), "Invalid enum value");
        } else {
            errors.put(cause.getPath().get(0).getFieldName(), "Invalid type provided");
        }
        return ResponseHandler.generateErrorResponse(errors, HttpStatus.BAD_REQUEST, "Validation Error");
    }

    // handle bad request errors
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleInvalidArgument(BadRequestException e) {
        return ResponseHandler.generateErrorResponse(null, HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedAccountAccessException.class)
    public ResponseEntity<Object> handleUnauthorizedAccountAccessException(UnauthorizedAccountAccessException e) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", e.getMessage());
        return ResponseHandler.generateErrorResponse(errors, HttpStatus.UNAUTHORIZED, "Unauthorized Account Access");
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedTransactionAccessException.class)
    public ResponseEntity<Object> handleUnauthorizedTransactionAccessException(UnauthorizedTransactionAccessException e) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", e.getMessage());
        return ResponseHandler.generateErrorResponse(errors, HttpStatus.UNAUTHORIZED, "Unauthorized Transaction Access");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleDisallowedFieldException(DisallowedFieldException e) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", e.getMessage());
        return ResponseHandler.generateErrorResponse(errors, HttpStatus.BAD_REQUEST, "Disallowed Field");
    }
}