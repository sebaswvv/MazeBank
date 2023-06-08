Feature: Everything accounts

    Scenario: As an employee I want to create a new account for a user
        Given I have a valid token for role "employee"
        When I create a new checkings account for user 5
        Then I should get a 201 status code

    Scenario: As an employee I want to disable an account
        Given I have a valid token for role "employee"
        When I disable account 2
        Then I should get a 200 status code

    Scenario: As an employee I want to enable an account
        Given I have a valid token for role "employee"
        When I enable account 2
        Then I should get a 200 status code

    Scenario: As an employee I want to configure an absolute limit for an account
        Given I have a valid token for role "employee"
        When I configure an absolute limit of -1000.0 for account 2
        Then I should get a 200 status code and an account response with an absolute limit of -1000.0