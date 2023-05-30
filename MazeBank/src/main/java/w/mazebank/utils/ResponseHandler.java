package w.mazebank.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseHandler {
    public static ResponseEntity<Object> generateErrorResponse(Map<String, String> errors, HttpStatus status, String message) {
        // KIJKEN OF WE HET IN DEZE FORMAT WILLEN?! NET ZOALS IN SWAGGER
        // Map<String, Object> map = new HashMap<String, Object>();
        // map.put("message", message);
        // map.put("message", errors);
        return new ResponseEntity<Object>(errors, status);
    }
}
