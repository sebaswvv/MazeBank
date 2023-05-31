package w.mazebank.cucumberglue;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import w.mazebank.enums.RoleType;
import w.mazebank.models.User;
import w.mazebank.services.JwtService;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BaseStepDefinitions {
    @LocalServerPort
    String port;
    ResponseEntity<String> lastResponse;
    // public static final String VALID_TOKEN_USER = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiYXV0aCI6W10sImlhdCI6MTY1MzMxMTc0NiwiZXhwIjoxNjg0ODQ3NzQ2fQ.itSjs-evCYi2P7JAKwT4DY8u5RIASTghoaeQOa33v_s";
    // public static final String VALID_TOKEN_ADMIN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyM0BleGFtcGxlLmNvbSIsImlhdCI6MTY4NTQ3Mjc1NSwiZXhwIjoxNjg2OTQzOTg0fQ.YlQjhmjukbtIvxnmi0-44wc-ZdNupE3cLeS3jBz-r9c";
    // public static final String EXPIRED_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiYXV0aCI6W10sImlhdCI6MTY1MzMxMTkwNSwiZXhwIjoxNjUzMzExOTA1fQ.mKFrXM15WCXVNbSFNpqYix_xsMjsH_M31hiFf-o7JXs";
    // public static final String INVALID_TOKEN = "invalid";

    protected  HttpHeaders httpHeaders;
    protected   RestTemplate restTemplate;
    protected JwtService jwtService;

    protected User employee;
    protected User customer;
    protected String token;
    protected ObjectMapper objectMapper;

    public BaseStepDefinitions() {
        objectMapper = new ObjectMapper();
        restTemplate = new RestTemplate();
        jwtService = new JwtService();
        httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");

        // create users
        employee = new User(4, "user3@example.com", 456123789, "Jim", "John", "$2a$10$CHn7sYgipDQqx4yvV.X59.c07V9sTDiGmKfnlEBz48yznkDm7o6a.", "0987654321", RoleType.EMPLOYEE, LocalDate.now().minusYears(30), LocalDateTime.now(), 5000, 200, false, null);
        customer = new User(3, "user2@example.com", 987654321, "Jane", "Smith", "$2a$10$CHn7sYgipDQqx4yvV.X59.c07V9sTDiGmKfnlEBz48yznkDm7o6a.", "0987654321", RoleType.CUSTOMER, LocalDate.now().minusYears(30), LocalDateTime.now(), 5000, 200, false, null);
    }
}
