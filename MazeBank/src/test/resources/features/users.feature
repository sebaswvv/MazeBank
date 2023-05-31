Feature: Everything Users

    Scenario: Get all users
        Given I have a valid token for role "employee"
        When I call the users endpoint
        Then the result is a list of user of size 6

    Scenario: Patch user daylimit
        Given I have a valid token for role "employee"
        When I call the users endpoint with a patch request
        Then the result is a user with a daylimit of 10000.0

    Scenario: Get the total balance of a user (Savings + Checking)
        Given I have a valid token for role "customer"
        When I call the users endpoint "/users/3/balance" with a get request
        Then the result is a user with a total balance of 12000.0, a savings balance of 10000.0, and a checking balance of 2000.0