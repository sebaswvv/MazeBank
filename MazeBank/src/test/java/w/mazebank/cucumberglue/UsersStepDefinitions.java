package w.mazebank.cucumberglue;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import w.mazebank.models.requests.UserPatchRequest;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UsersStepDefinitions extends BaseStepDefinitions {
    @Given("^I have a valid token for role \"([^\"]*)\"$")
    public void Test(String role) {
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
        try {
            lastResponse = restTemplate.exchange(
                "http://localhost:" + port + endpoint,
                HttpMethod.GET, // Adjust the HTTP method if necessary
                requestEntity,
                String.class
            );
        } catch (HttpClientErrorException ex) {
            // pass the response to the lastResponse variable
            lastResponse = new ResponseEntity<>(ex.getResponseBodyAsString(), ex.getResponseHeaders(), ex.getStatusCode().value());
        }
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

    @When("I call the users endpoint {string} with a patch request and a transactionLimit of {double}")
    public void iCallTheUsersEndpointWithAPatchRequestAndATransactionLimitOf(String endpoint, double transactionLimit) {
        token = jwtService.generateToken(employee);

        UserPatchRequest userPatchRequest = new UserPatchRequest();

        httpHeaders.clear();
        httpHeaders.add("Authorization", "Bearer " + token);

        // Create the HTTP entity with the request body and headers
        HttpEntity<Object> requestEntity = new HttpEntity<>(userPatchRequest, httpHeaders);

        // Send the request
        HttpClient client = HttpClientBuilder.create().build();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
        lastResponse = restTemplate.exchange(
            "http://localhost:" + port + endpoint,
            HttpMethod.PATCH,
            requestEntity,
            String.class
        );
    }

    @Then("the result is a user with a transactionLimit of {double}")
    public void theResultIsAUserWithATransactionLimitOf(double transactionLimit) {
        Assertions.assertEquals(transactionLimit, JsonPath.read(lastResponse.getBody(), "$.transactionLimit"));
    }

    @Then("the result is a list of accounts of size {int}")
    public void theResultIsAListOfAccountsOfSize(int size) {
        assert lastResponse.getBody() != null;
        List<Object> accounts = JsonPath.read(lastResponse.getBody(), "$");
        assertEquals(size, accounts.size());
    }

    @Then("the result is a list of transactions of size {int}")
    public void theResultIsAListOfTransactionsOfSize(int size) {
        assert lastResponse.getBody() != null;
        List<Object> transactions = JsonPath.read(lastResponse.getBody(), "$");
        assertEquals(size, transactions.size());
    }

    @Then("the response status code is {int} with message {string}")
    public void responseStatusCodeIsWithMessage(int statusCode, String errorMessage) {
        // Assert the response status code
        assertEquals(statusCode, lastResponse.getStatusCode().value());

        // Assert the response body
        assertTrue(lastResponse.getBody().contains(errorMessage));
    }
}
