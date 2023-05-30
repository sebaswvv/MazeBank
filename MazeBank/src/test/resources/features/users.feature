Feature: Everything Users

    Scenario: Getting all guitars
        Given I have a valid token for role "employee"
        When I call the users endpoint
        Then the result is a list of user of size 6