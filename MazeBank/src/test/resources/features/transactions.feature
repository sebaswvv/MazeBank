Feature: Everything transactions

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