package w.mazebank.advices;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import w.mazebank.exceptions.UserNotFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApplicationExceptionHandler {
    // handle User Not Found Exception
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public Map<String, String> handleUserNotFoundException(UserNotFoundException e) {
        // create new "json" for error message
        Map<String, String> errors = new HashMap<>();
        errors.put("message", e.getMessage());
        return errors;
    }
}