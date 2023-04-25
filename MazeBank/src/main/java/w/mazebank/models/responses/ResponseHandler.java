package w.mazebank.models.responses;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseHandler {
    public static ResponseEntity<Object> generateResponse(String message, HttpStatus status, Object responseObj) {
        Map<String, Object> map = new HashMap<String, Object>();

        // add the message, status, and data to the map
        map.put("message", message);
        map.put("status", status.value());
        map.put("data", responseObj);


        // if the given object is an iterable, add the results amount to the map
        if (responseObj instanceof Iterable<?> iterable) {
            int size = 0;
            for (Object obj : iterable) {
                size++;
            }
            map.put("results", size);
        }

        return new ResponseEntity<Object>(map, status);
    }
}
