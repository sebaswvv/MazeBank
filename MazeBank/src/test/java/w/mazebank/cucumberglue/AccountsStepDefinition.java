package w.mazebank.cucumberglue;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import w.mazebank.enums.AccountType;
import w.mazebank.models.requests.AccountRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountsStepDefinition extends BaseStepDefinitions {
    @When("I create a new checkings account for user {int}")
    public void iCreateANewCheckingsAccountForUser(int userId) {
        token = jwtService.generateToken(employee);

        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setUserId(userId);
        accountRequest.setAccountType(AccountType.CHECKING);
        accountRequest.setActive(true);
        accountRequest.setAbsoluteLimit(-5000);

        // call the endpoint /accounts with a POST request
        httpHeaders.clear();
        httpHeaders.add("Authorization", "Bearer " + token);

        // Create the HTTP entity with the request body and headers
        HttpEntity<Object> requestEntity = new HttpEntity<>(accountRequest, httpHeaders);

        // Send the request with the accountRequest as the body
        lastResponse = restTemplate.exchange(
            "http://localhost:" + port + "/accounts",
            HttpMethod.POST, // Adjust the HTTP method if necessary
            requestEntity,
            String.class
        );
    }

    @Then("I should get a {int} status code")
    public void iShouldGetAStatusCode(int statusCode) {
        assertEquals(statusCode, lastResponse.getStatusCodeValue());
    }

    @When("I disable account {int}")
    public void iDisableAccount(int accountId) {
        token = jwtService.generateToken(employee);

        // call the endpoint /accounts/{accountId} with a PATCH request
        httpHeaders.clear();
        httpHeaders.add("Authorization", "Bearer " + token);

        // Create the HTTP entity with the request body and headers
        HttpEntity<Object> requestEntity = new HttpEntity<>(null, httpHeaders);

        // Send the request with the accountRequest as the body
        lastResponse = restTemplate.exchange(
            "http://localhost:" + port + "/accounts/" + accountId + "/disable",
            HttpMethod.PUT, // Adjust the HTTP method if necessary
            requestEntity,
            String.class
        );
    }

    @When("I enable account {int}")
    public void iEnableAccount(int accountId) {
        token = jwtService.generateToken(employee);

        // call the endpoint /accounts/{accountId} with a PATCH request
        httpHeaders.clear();
        httpHeaders.add("Authorization", "Bearer " + token);

        // Create the HTTP entity with the request body and headers
        HttpEntity<Object> requestEntity = new HttpEntity<>(null, httpHeaders);

        // Send the request with the accountRequest as the body
        lastResponse = restTemplate.exchange(
            "http://localhost:" + port + "/accounts/" + accountId + "/enable",
            HttpMethod.PUT, // Adjust the HTTP method if necessary
            requestEntity,
            String.class
        );
    }
}
