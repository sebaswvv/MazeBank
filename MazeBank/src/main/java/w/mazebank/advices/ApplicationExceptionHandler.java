package w.mazebank.advices;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    // map errors to a map with a message field
    private Map<String, String> mapErrors(Exception e) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", e.getMessage());
        return errors;
    }

    // handle Not Found Exception
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(NotFoundException e) {
        return ResponseHandler.generateErrorResponse(mapErrors(e), HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(UnauthorizedUserAccessException.class)
    public ResponseEntity<Object> handleUnauthorizedUserAccessException(UnauthorizedUserAccessException e) {
        return ResponseHandler.generateErrorResponse(mapErrors(e), HttpStatus.FORBIDDEN);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException e) {
        return ResponseHandler.generateErrorResponse(mapErrors(e), HttpStatus.FORBIDDEN);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleInvalidArgument(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put("message", error.getDefaultMessage());
        });

        return ResponseHandler.generateErrorResponse(errors, HttpStatus.BAD_REQUEST);
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
        return ResponseHandler.generateErrorResponse(errors, HttpStatus.BAD_REQUEST);
    }

    // handle bad request errors
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleInvalidArgument(BadRequestException e) {
        return ResponseHandler.generateErrorResponse(mapErrors(e), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(UnauthorizedAccountAccessException.class)
    public ResponseEntity<Object> handleUnauthorizedAccountAccessException(UnauthorizedAccountAccessException e) {
        return ResponseHandler.generateErrorResponse(mapErrors(e), HttpStatus.FORBIDDEN);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(UnauthorizedTransactionAccessException.class)
    public ResponseEntity<Object> handleUnauthorizedTransactionAccessException(UnauthorizedTransactionAccessException e) {
        return ResponseHandler.generateErrorResponse(mapErrors(e), HttpStatus.FORBIDDEN);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DisallowedFieldException.class)
    public ResponseEntity<Object> handleDisallowedFieldException(DisallowedFieldException e) {
        return ResponseHandler.generateErrorResponse(mapErrors(e), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(TransactionFailedException.class)
    public ResponseEntity<Object> handleTransactionFailedException(TransactionFailedException e) {
        return ResponseHandler.generateErrorResponse(mapErrors(e), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<Object> handleInsufficientFundsException(InsufficientFundsException e) {
        return ResponseHandler.generateErrorResponse(mapErrors(e), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserHasAccountsException.class)
    public ResponseEntity<Object> handleUserHasAccountsException(UserHasAccountsException e) {
        return ResponseHandler.generateErrorResponse(mapErrors(e), HttpStatus.BAD_REQUEST);
    }

    // BadCredentialsException = 401
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException e) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Invalid username or password");
        return ResponseHandler.generateErrorResponse(errors, HttpStatus.UNAUTHORIZED);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Object> handleUsernameNotFoundException(UsernameNotFoundException e) {
        return ResponseHandler.generateErrorResponse(mapErrors(e), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ResponseEntity<Object> handleEmailAlreadyUsedException(EmailAlreadyUsedException e) {
        return ResponseHandler.generateErrorResponse(mapErrors(e), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BsnAlreadyUsedException.class)
    public ResponseEntity<Object> handleBsnAlreadyUsedException(BsnAlreadyUsedException e) {
        return ResponseHandler.generateErrorResponse(mapErrors(e), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserNotOldEnoughException.class)
    public ResponseEntity<Object> handleUserNotOldEnoughException(UserNotOldEnoughException e) {
        return ResponseHandler.generateErrorResponse(mapErrors(e), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AccountLockOrUnlockStatusException.class)
    public ResponseEntity<Object> handleAccountLockOrUnlockStatusException(AccountLockOrUnlockStatusException e) {
        return ResponseHandler.generateErrorResponse(mapErrors(e), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AccountCreationLimitReachedException.class)
    public ResponseEntity<Object> handleAccountCreationLimitReachedException(AccountCreationLimitReachedException e) {
        return ResponseHandler.generateErrorResponse(mapErrors(e), HttpStatus.BAD_REQUEST);
    }
}