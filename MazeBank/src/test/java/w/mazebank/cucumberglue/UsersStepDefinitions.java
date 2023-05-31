package w.mazebank.cucumberglue;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.junit.jupiter.api.Assertions;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import w.mazebank.enums.RoleType;
import w.mazebank.models.User;
import w.mazebank.models.requests.UserPatchRequest;
import w.mazebank.services.JwtService;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

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
            HttpMethod.PATCH,
            requestEntity,
            String.class
        );
    }

    @Then("the result is a user with a daylimit of {double}")
    public void theResultIsAUserWithADaylimitOf(double expectedDayLimit) throws JsonProcessingException {
        assert lastResponse.getBody() != null;
        Object dayLimit = JsonPath.read(lastResponse.getBody(), "$.dayLimit");
        assertEquals(expectedDayLimit, dayLimit);
    }

    @When("I call the users endpoint {string} with a get request")
    public void iCallTheUsersEndpointWithAGetRequest(String endpoint) {
        httpHeaders.clear();
        httpHeaders.add("Authorization", "Bearer " + token);

        // Create the HTTP entity with the request body and headers
        HttpEntity<Object> requestEntity = new HttpEntity<>(null, httpHeaders);

        // Send the request
        lastResponse = restTemplate.exchange(
            "http://localhost:" + port + endpoint,
            HttpMethod.GET, // Adjust the HTTP method if necessary
            requestEntity,
            String.class
        );
    }

    @Then("the result is a user with a total balance of {double}, a savings balance of {double}, and a checking balance of {double}")
    public void theResultIsAUserWithATotalBalanceOfASavingsBalanceOfAndACheckingBalanceOf(double totalBalance, double savingsBalance, double checkingBalance) throws JsonProcessingException {
        assert lastResponse.getBody() != null;
        Object total = JsonPath.read(lastResponse.getBody(), "$.totalBalance");
        Object savings = JsonPath.read(lastResponse.getBody(), "$.savingsBalance");
        Object checking = JsonPath.read(lastResponse.getBody(), "$.checkingBalance");
        assertEquals(totalBalance, total);
        assertEquals(savingsBalance, savings);
        assertEquals(checkingBalance, checking);
    }


    @Then("the result is a user with a email of {string}, a firstname of {string}, a lastname of {string}, a dayLimit of {double}, and a role of {string}, amountRemaining of {double}")
    public void theResultIsAUserWithAEmailOfAFirstnameOfALastnameOfADayLimitOfAndARoleOfAmountRemainingOf(String email, String firstName, String lastName, double dayLimit, String role, double amountRemaining) {
        assert lastResponse.getBody() != null;
        Object emailResult = JsonPath.read(lastResponse.getBody(), "$.email");
        Object firstNameResult = JsonPath.read(lastResponse.getBody(), "$.firstName");
        Object lastNameResult = JsonPath.read(lastResponse.getBody(), "$.lastName");
        Object dayLimitResult = JsonPath.read(lastResponse.getBody(), "$.dayLimit");
        Object roleResult = JsonPath.read(lastResponse.getBody(), "$.role");
        Object amountRemainingResult = JsonPath.read(lastResponse.getBody(), "$.amountRemaining");
        assertEquals(email, emailResult);
        assertEquals(firstName, firstNameResult);
        assertEquals(lastName, lastNameResult);
        assertEquals(dayLimit, dayLimitResult);
        assertEquals(role, roleResult);
        assertEquals(amountRemaining, amountRemainingResult);
    }
}
