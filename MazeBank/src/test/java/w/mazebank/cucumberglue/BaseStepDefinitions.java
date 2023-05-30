package w.mazebank.cucumberglue;

import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

public class BaseStepDefinitions {
    @LocalServerPort
    String port;
    ResponseEntity<String> lastResponse;
}
