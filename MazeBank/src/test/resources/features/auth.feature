Feature: User Registration

    Scenario: User registers an account
        When the client calls endpoint "/auth/register"
        Then response status code is 201
        And the client should receive a response body matching json:
        """
        {
            "authenticationToken": "\\S+"
        }
        """

