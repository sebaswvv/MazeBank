Feature: Everything Users

    Scenario: Get all users
        Given I have a valid token for role "employee"
        When I call the users endpoint
        Then the result is a list of user of size 6

    Scenario: Get user by id
        Given I have a valid token for role "customer"
        When I call the users endpoint "/users/3" with a get request
        Then the result is a user with a email of "user2@example.com", a firstname of "Jane", a lastname of "Smith", a dayLimit of 5000.0, and a role of "CUSTOMER", amountRemaining of 5000.0

    Scenario: As a customer, I want to be able to access my account details, transaction history, and user details.
        Given I have a valid token for role "customer"
        When I call the users endpoint "/users/3" with a get request
        Then the result is a user with a email of "user2@example.com", a firstname of "Jane", a lastname of "Smith", a dayLimit of 5000.0, and a role of "CUSTOMER", amountRemaining of 5000.0
        When I call the users endpoint "/users/3/accounts" with a get request
        Then the result is a list of accounts of size 2
        When I call the users endpoint "/users/3/transactions" with a get request
        Then the result is a list of transactions of size 6

    Scenario: As a bank, I want to make sure that other customers cannot access a customer’s account details, transaction history, and user details.
        Given I have a valid token for role "customer"
        When I call the users endpoint "/users/2" with a get request
        # this message is returned when the performing user is not authorized to access this
        Then the response status code is 404 with message "user not found with id: 2"
        When I call the users endpoint "/users/2/accounts" with a get request
        Then the response status code is 403 with message "user not allowed to access accounts of user with id: 2"
        # this message is returned when the performing user is not authorized to access this
        When I call the users endpoint "/users/2/transactions" with a get request
        Then the response status code is 404 with message "user not found with id: 2"

    Scenario: Patch user daylimit
        Given I have a valid token for role "employee"
        When I call the users endpoint with a patch request
        Then the result is a user with a daylimit of 1000.0

    Scenario: Get the total balance of a user (Savings + Checking)
        Given I have a valid token for role "customer"
        When I call the users endpoint "/users/3/balance" with a get request
        Then the result is a user with a total balance of 12200.0, a savings balance of 10000.0, and a checking balance of 2200.0


    Scenario: Get all users that have no accounts
        Given I have a valid token for role "employee"
        When I call the users endpoint "/users?withoutAccounts=true" with a get request
        Then the result is a list of user of size 2


    Scenario: As an employee, I want to configure the transaction limit for a customer
        Given I have a valid token for role "employee"
        When I call the users endpoint "/users/3" with a patch request and a transactionLimit of 500.0
        Then the result is a user with a transactionLimit of 500.0

