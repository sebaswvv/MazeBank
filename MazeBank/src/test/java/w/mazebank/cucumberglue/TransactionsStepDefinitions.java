package w.mazebank.cucumberglue;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import w.mazebank.models.responses.TransactionResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TransactionsStepDefinitions extends BaseStepDefinitions{

    @When("I call the transactions endpoint with a {string} and {string} parameter")
    public void iCallTheTransactionsEndpointWithAStartDateAndEndDateParameter(String startDate, String endDate) throws ParseException {
        // string to date
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
        Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse(endDate);

        // call endpoint UserController getTransactionsByUserId
        lastResponse = lastResponse = restTemplate.getForEntity(
            "/transactions?startDate={startDate}&endDate={endDate}",
            String.class,
            date,
            date2
        );


        System.out.println("startDate: " + date);
        System.out.println("endDate: " + date2);
    }

    @Then("I should see a list of {int} transactions within the specified date range")
    public void iShouldSeeAListOfTransactionsWithinTheSpecifiedDateRange(int listSizeOfTransactions) {
        // assert lastResponse.getBody() != null;
        System.out.println(lastResponse.getBody());
    }
}
