Feature: Everything Users

    Scenario: Get all users
        Given I have a valid token for role "employee"
        When I call the users endpoint
        Then the result is a list of user of size 6

    Scenario: Patch user daylimit
        Given I have a valid token for role "employee"
        When I call the users endpoint with a patch request
        Then the result is a user with a daylimit of 10000.0