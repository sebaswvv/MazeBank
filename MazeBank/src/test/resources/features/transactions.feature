Feature: Everything Transactions

    Scenario: Search transactions between a date range
        Given I have a valid token for role "customer"
        When I call the transactions endpoint with a "2022-01-01" and "2023-12-12" parameter
        Then I should see a list of 2 transactions within the specified date range

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

    Scenario: Make a transaction from a savings account to another customer (400)
        Given I have a valid token for role "customer"
        And I have a savings account with balance 1000.00
        When I make a transaction from my savings account to customer 3
        Then response status code is 400 with message "Cannot transfer from a savings account to an account that is not of the same customer"

    Scenario: Make a transaction from another customer to a savings account (400)
        Given I have a valid token for role "employee"
        When I make a transaction from customer 3  to a savings account
        Then response status code is 400 with message "Cannot transfer to a savings account from an account that is not of the same customer"

    Scenario: Make a transaction, as an employee, for a customer
        Given I have a valid token for role "employee"
        When I make a transaction from account 1 to account 4
        Then the result is a 201 status code. and a transaction with id 4, amount 100.00, from account with iban "NL76INHO0493458014" to account with iban "NL76INHO0493458018"