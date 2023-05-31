package w.mazebank.cucumberglue;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import w.mazebank.enums.AccountType;
import w.mazebank.models.Account;
import w.mazebank.models.User;
import w.mazebank.models.requests.TransactionRequest;
import w.mazebank.utils.IbanGenerator;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TransactionsStepDefinitions extends BaseStepDefinitions {
    private Account savingsAccount;
    @And("I have a savings account with balance {double}")
    public void iHaveASavingsAccountWithBalance(double balance) {

        // create a savings account
        savingsAccount = Account.builder()
                .id(1)
                .iban(IbanGenerator.generate())
                .accountType(AccountType.SAVINGS)
                .balance(1000.00)
                .build();

        // add the savings account to the customer
        customer.setAccounts(List.of(savingsAccount));
    }

    @When("I make a transaction from my savings account to customer {int}")
    public void iMakeATransactionFromMySavingsAccountToCustomer(int receivingUserId) {
        // create body for transactionRequest
        TransactionRequest transactionRequest = TransactionRequest.builder()
                .amount(100.00)
                .receiverIban("NL76INHO0493458014")
                .senderIban("NL76INHO0493458015")
                .build();

        httpHeaders.clear();

        token = jwtService.generateToken(customer);

        httpHeaders.add("Authorization", "Bearer " + token);

        // Create the HTTP entity with the request body and headers
        HttpEntity<Object> requestEntity = new HttpEntity<>(transactionRequest, httpHeaders);

        try{
            // Send the request
            lastResponse = restTemplate.exchange(
                "http://localhost:" + port + "/transactions",
                HttpMethod.POST,
                requestEntity,
                String.class
            );
        }
        catch (HttpClientErrorException.BadRequest ex) {
            // pass the response to the lastResponse variable
            lastResponse = new ResponseEntity<>(ex.getResponseBodyAsString(), ex.getResponseHeaders(), ex.getRawStatusCode());
        }

    }

    @Then("response status code is {int} with message {string}")
    public void responseStatusCodeIsWithMessage(int statusCode, String errorMessage) {
        // Assert the response status code
        assertEquals(statusCode, lastResponse.getStatusCodeValue());

        // Assert the response body
        assertTrue(lastResponse.getBody().contains(errorMessage));
    }
}
