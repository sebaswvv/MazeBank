package w.mazebank.cucumberglue;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

public class BaseStepDefinitions {
    @LocalServerPort
    String port;
    ResponseEntity<String> lastResponse;
    public static final String VALID_TOKEN_USER = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiYXV0aCI6W10sImlhdCI6MTY1MzMxMTc0NiwiZXhwIjoxNjg0ODQ3NzQ2fQ.itSjs-evCYi2P7JAKwT4DY8u5RIASTghoaeQOa33v_s";
    public static final String VALID_TOKEN_ADMIN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyM0BleGFtcGxlLmNvbSIsImlhdCI6MTY4NTQ3Mjc1NSwiZXhwIjoxNjg2OTQzOTg0fQ.YlQjhmjukbtIvxnmi0-44wc-ZdNupE3cLeS3jBz-r9c";
    public static final String EXPIRED_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiYXV0aCI6W10sImlhdCI6MTY1MzMxMTkwNSwiZXhwIjoxNjUzMzExOTA1fQ.mKFrXM15WCXVNbSFNpqYix_xsMjsH_M31hiFf-o7JXs";
    public static final String INVALID_TOKEN = "invalid";

    public final HttpHeaders httpHeaders = new HttpHeaders();
    public final TestRestTemplate restTemplate = new TestRestTemplate();
}
