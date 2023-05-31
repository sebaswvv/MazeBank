package w.mazebank.cucumberglue;


import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import w.mazebank.enums.RoleType;
import w.mazebank.models.User;
import w.mazebank.models.requests.UserPatchRequest;
import w.mazebank.models.responses.TransactionResponse;
import w.mazebank.services.JwtService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class UsersStepDefinitions extends BaseStepDefinitions{
    @Given("^I have a valid token for role \"([^\"]*)\"$")
    public void Test(String role){
        switch (role) {
            case "customer" -> token = jwtService.generateToken(customer);
            case "employee" -> token = jwtService.generateToken(employee);
            default -> throw new IllegalArgumentException("No such role");
        }
    }

    @When("I call the users endpoint")
    public void iCallTheUsersEndpoint() {
        httpHeaders.clear();
        httpHeaders.add("Authorization", "Bearer " + token);

        // Create the HTTP entity with the request body and headers
        HttpEntity<Object> requestEntity = new HttpEntity<>(null, httpHeaders);

        // Send the request
        lastResponse = restTemplate.exchange(
            "http://localhost:" + port + "/users",
            HttpMethod.GET, // Adjust the HTTP method if necessary
            requestEntity,
            String.class
        );
    }

    @Then("the result is a list of user of size {int}")
    public void theResultIsAListOfUserOfSize(int size) {
        assert lastResponse.getBody() != null;
    }

    @When("I call the users endpoint with a patch request")
    public void iCallTheUsersEndpointWithAPatchRequest() {
        httpHeaders.clear();
        httpHeaders.add("Authorization", "Bearer " + token);

        // allow patch requests
        httpHeaders.add("Access-Control-Allow-Methods", "PATCH");

        // create userPatchRequest
        UserPatchRequest userPatchRequest = new UserPatchRequest();
        userPatchRequest.setDayLimit(10000.00);

        // Create the HTTP entity with the request body and headers
        HttpEntity<Object> requestEntity = new HttpEntity<>(userPatchRequest, httpHeaders);

        // Send the request
        HttpClient client = HttpClientBuilder.create().build();

        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
        lastResponse = restTemplate.exchange(
            "http://localhost:" + port + "/users/2",
            HttpMethod.PATCH, // Adjust the HTTP method if necessary
            requestEntity,
            String.class
        );
    }

    @Then("the result is a user with a daylimit of {int}")
    public void theResultIsAUserWithADaylimitOf(int expectedDayLimit) {
        assert lastResponse.getBody() != null;
    }



}
