
Feature: Everything Transactions

    Scenario: Search transactions between a date range
        Given I have a valid token for role "customer"
        When I call the transactions endpoint with a "2023-01-01" and "2023-01-02" parameter
        Then I should see a list of 3 transactions within the specified date range

#    Scenario: Search transactions from/to specific IBAN
#        Given I have a valid token for role "customer"
#        When I call the accounts/search/{name} endpoint with an IBAN parameter
#        Then I should see a list of transactions with the specified IBAN
#
#    Scenario: Search transactions with balances less than a certain amount
#        Given I have a valid token for role "customer"
#        When I call the transactions endpoint with balance < maxAmount
#        Then I should see a list of transactions with balances less than maxAmount
#
#    Scenario: Search transactions with balances equal to a certain amount
#        Given I have a valid token for role "customer"
#        When I call the transactions endpoint with balance == amount
#        Then I should see a list of transactions with balances equal to amount
#
#    Scenario: Search transactions with balances greater than a certain amount
#        Given I have a valid token for role "customer"
#        When I call the transactions endpoint with balance > minAmount
#        Then I should see a list of transactions with balances greater than minAmount