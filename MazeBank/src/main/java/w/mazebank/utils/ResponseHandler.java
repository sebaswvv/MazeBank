package w.mazebank.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class ResponseHandler {
    public static ResponseEntity<Object> generateErrorResponse(Map<String, String> errors, HttpStatus status) {
        return new ResponseEntity<>(errors, status);
    }
}
