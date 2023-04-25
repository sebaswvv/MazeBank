package w.mazebank.advices;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import w.mazebank.exceptions.UserNotFoundException;
import w.mazebank.models.responses.ResponseHandler;

@RestControllerAdvice
public class ApplicationExceptionHandler {
    // handle general exception
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception e) {
        return ResponseHandler.generateResponse(e.getMessage(), HttpStatus.MULTI_STATUS, null);
    }

    // handle User Not Found Exception
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException e) {
        return ResponseHandler.generateResponse(e.getMessage(), HttpStatus.MULTI_STATUS, null);
    }
}