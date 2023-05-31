package w.mazebank.controllers;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.test.context.support.WithMockUser;
import w.mazebank.enums.AccountType;
import w.mazebank.enums.RoleType;
import w.mazebank.enums.TransactionType;
import w.mazebank.exceptions.*;
import w.mazebank.models.Account;
import w.mazebank.models.User;
import w.mazebank.models.requests.AccountPatchRequest;
import w.mazebank.models.responses.AccountResponse;
import w.mazebank.models.responses.IbanResponse;
import w.mazebank.models.responses.TransactionResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
class AccountControllerTest extends BaseControllerTest{

    @Test
    void getAccountsByNamHappyFlowReturns200() throws Exception {
        // create a list of IbanResponse objects
        List<IbanResponse> list = List.of(
            IbanResponse.builder()
                .iban("NL01INHO123456789")
                .firstName("John")
                .lastName("Doe")
                .build(),
            IbanResponse.builder()
                .iban("NL01INHO123456788")
                .firstName("Jane")
                .lastName("Doe")
                .build()
        );

        // mock
        when(accountService.getAccountsByName("John Doe")).thenReturn(list);

        // perform
        mockMvc.perform(get("/accounts/search/John Doe")
                .header("Authorization", "Bearer " + customerToken)
                .with(csrf())
                .with(user(authCustomer)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].iban").value("NL01INHO123456789"))
            .andExpect(jsonPath("$[0].firstName").value("John"))
            .andExpect(jsonPath("$[0].lastName").value("Doe"))
            .andExpect(jsonPath("$[1].iban").value("NL01INHO123456788"))
            .andExpect(jsonPath("$[1].firstName").value("Jane"))
            .andExpect(jsonPath("$[1].lastName").value("Doe"));
        ;
    }

    @Test
    void getAccountsByNameWithoutBeingLoggedInReturns401() throws Exception {
        // perform
        mockMvc.perform(get("/accounts/search/John Doe")
                .with(csrf())
                .with(user(authCustomer)))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllAccountsThrows200() throws Exception {
        // user to add to the accounts
        User user = User.builder()
            .id(1)
            .firstName("John")
            .lastName("Doe")
            .role(RoleType.CUSTOMER)
            .blocked(false)
            .createdAt(LocalDateTime.now())
            .build();

        // list of all the accounts to mock the database
        List<AccountResponse> accounts = List.of(
            AccountResponse.builder()
                .id(1)
                .accountType(AccountType.CHECKING.getValue())
                .balance(0.0)
                .active(true)
                .iban("NL01INHO123456789")
                .absoluteLimit(0.0)
                .build(),
            AccountResponse.builder()
                .id(2)
                .accountType(AccountType.SAVINGS.getValue())
                .balance(0.0)
                .active(true)
                .iban("NL01INHO123456789")
                .absoluteLimit(0.0)
                .build()
        );

        // mock
        when(accountService.getAllAccounts(0, 10, "ASC", "")).thenReturn(accounts);

        // NOTE: ik moet in de url de offset en limit meegeven, anders krijg ik geen response body!!
        mockMvc.perform(get("/accounts?offsete=0&limit=10&sort=ASC&search=")
                .header("Authorization", "Bearer " + employeeToken)
                .with(csrf())
                .with(user(authEmployee))
                .contentType("application/json")
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].accountType").value(AccountType.CHECKING.getValue()))
            .andExpect(jsonPath("$[0].balance").value(0.0))
            .andExpect(jsonPath("$[0].active").value(true))
            .andExpect(jsonPath("$[0].iban").value("NL01INHO123456789"))
            .andExpect(jsonPath("$[0].absoluteLimit").value(0.0))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].accountType").value(AccountType.SAVINGS.getValue()))
            .andExpect(jsonPath("$[1].balance").value(0.0))
            .andExpect(jsonPath("$[1].active").value(true))
            .andExpect(jsonPath("$[1].iban").value("NL01INHO123456789"))
            .andExpect(jsonPath("$[1].absoluteLimit").value(0.0))
            .andReturn();
    }

    @Test
    void postAcccountShouldReturnStatusCreatedAndObject() throws Exception {
        Account account = Account.builder()
            .accountType(AccountType.CHECKING)
            .balance(0.0)
            .isActive(true)
            .absoluteLimit(0.0)
            .createdAt(null)
            .build();
        AccountResponse accountResponse = AccountResponse.builder()
            .id(1)
            .accountType(AccountType.CHECKING.getValue())
            .balance(0.0)
            .active(true)
            .iban("NL01INHO123456789")
            .absoluteLimit(0.0)
            .build();

        // when(userServiceJpa.getUserById(Mockito.anyLong())).thenReturn(authUser);
        when(accountService.createAccount(Mockito.any())).thenReturn(accountResponse);

        mockMvc.perform(post("/accounts")
                .header("Authorization", "Bearer " + employeeToken)
                .with(csrf())
                .with(user(authEmployee))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(account))
            ).andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.accountType").value(AccountType.CHECKING.getValue()))
            .andExpect(jsonPath("$.balance").value(0.0))
            .andExpect(jsonPath("$.active").value(true))
            .andExpect(jsonPath("$.iban").value("NL01INHO123456789"))
            .andExpect(jsonPath("$.absoluteLimit").value(0.0));
    }

    @Test
    void getAccountByIdShouldReturnStatusOkAndObjectThrows200() throws Exception {
        User user = User.builder()
            .id(1)
            .firstName("John")
            .lastName("Doe")
            .role(RoleType.CUSTOMER)
            .blocked(false)
            .createdAt(LocalDateTime.now())
            .build();

        Account account = Account.builder()
            .id(1)
            .iban("NL01INHO0123456789")
            .accountType(AccountType.CHECKING)
            .balance(1000.0)
            .user(user)
            .isActive(true)
            .absoluteLimit(0.0)
            .createdAt(LocalDateTime.of(2023, 1, 1, 0, 0, 0))
            .build();

        // when(userRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(authUser));

        when(accountService.getAccountAndValidate(Mockito.any(), Mockito.any()))
            .thenReturn(account);

        mockMvc.perform(get("/accounts/1")
                .header("Authorization", "Bearer " + customerToken)
                .with(user(authCustomer))
            ).andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.iban").value("NL01INHO0123456789"))
            .andExpect(jsonPath("$.accountType").value(AccountType.CHECKING.getValue()))
            .andExpect(jsonPath("$.balance").value(1000.0))
            .andExpect(jsonPath("$.user.id").value(1))
            .andExpect(jsonPath("$.user.firstName").value("John"))
            .andExpect(jsonPath("$.user.lastName").value("Doe"))
            .andExpect(jsonPath("$.active").value(true))
            // .andExpect(jsonPath("$.createdAt").value("2023-01-01T00:00:00"))
            .andExpect(jsonPath("$.absoluteLimit").value(0.0));
    }

    @Test
    void depositHappyFlowThrows201() throws Exception {
        // create account for the authUser
        Account account = Account.builder()
            .id(1)
            .iban("NL01INHO0123456789")
            .accountType(AccountType.CHECKING)
            .balance(1000.0)
            .user(authCustomer)
            .isActive(true)
            .absoluteLimit(0.0)
            .createdAt(LocalDateTime.of(2023, 1, 1, 0, 0, 0))
            .build();

        // create AtmRequest
        JSONObject request = new JSONObject();
        request.put("amount", 100.0);

        // create the TransactionResponse that the service should return
        TransactionResponse transactionResponse = TransactionResponse.builder()
            .id(1L)
            .amount(100.0)
            .type(TransactionType.DEPOSIT.toString())
            .sender(null)
            .receiver(account.getIban())
            .userPerforming(authCustomer.getId())
            .timestamp(LocalDateTime.now().toString())
            .build();

        // mock the service
        when(accountService.deposit(1L, 100.0 , authCustomer)).thenReturn(transactionResponse);

        // call the controller
        mockMvc.perform(post("/accounts/1/deposit")
                .header("Authorization", "Bearer " + customerToken)
                .with(csrf())
                .with(user(authCustomer))
                .contentType("application/json")
                .content(request.toString())
            ).andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.amount").value(100.0))
            .andExpect(jsonPath("$.type").value(TransactionType.DEPOSIT.toString()))
            .andExpect(jsonPath("$.receiver").value("NL01INHO0123456789"))
            .andExpect(jsonPath("$.userPerforming").value(1))
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.timestamp").isNotEmpty());

    }

    @Test
    void depositUnauthorizedThrows401() throws Exception {

        // create AtmRequest
        JSONObject request = new JSONObject();
        request.put("amount", 100.0);

        // this error message: UnauthorizedAccountAccessException("You are not authorized to access this account");
        when(accountService.deposit(1L, 100.0 , authCustomer)).thenThrow(new UnauthorizedAccountAccessException("Unauthorized"));

        // call the controller
        mockMvc.perform(post("/accounts/1/deposit")
            .header("Authorization", "Bearer " + customerToken)
            .with(csrf())
            .with(user(authCustomer))
            .contentType("application/json")
            .content(request.toString())
        ).andDo(print())
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value("Unauthorized"));
    }

    @Test
    void depositWithNoExistingAccountThrows404() throws Exception {

        // create AtmRequest
        JSONObject request = new JSONObject();
        request.put("amount", 100.0);

        // this error message: "Account with id: 1 not found"
        when(accountService.deposit(1L, 100.0 , authCustomer)).thenThrow(new AccountNotFoundException("Account with id: 1 not found"));

        // call the controller
        mockMvc.perform(post("/accounts/1/deposit")
                .header("Authorization", "Bearer " + customerToken)
                .with(csrf())
                .with(user(authCustomer))
                .contentType("application/json")
                .content(request.toString())
            ).andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Account with id: 1 not found"));
    }

    @Test
    void patchAccountShouldReturn200() throws Exception {
        // create account for the authUser
        Account account = Account.builder()
            .id(1)
            .iban("NL01INHO0123456789")
            .accountType(AccountType.CHECKING)
            .balance(1000.0)
            .user(authCustomer)
            .isActive(true)
            .absoluteLimit(0.0)
            .createdAt(LocalDateTime.of(2023, 1, 1, 0, 0, 0))
            .build();

        AccountPatchRequest accountPatchRequest = AccountPatchRequest.builder()
            .absoluteLimit(100.0)
            .build();

        // create AtmRequest
        JSONObject request = new JSONObject();
        request.put("absoluteLimit", 100.0);

        // create the TransactionResponse that the service should return
        AccountResponse accountResponse = AccountResponse.builder()
            .id(1)
            .iban("NL01INHO0123456789")
            .accountType(AccountType.CHECKING.getValue())
            .balance(1000.0)
            .active(true)
            .absoluteLimit(100.0)
            .build();

        // mock the service
        when(accountService.updateAccount(1L, accountPatchRequest)).thenReturn(accountResponse);

        // call the controller
        mockMvc.perform(patch("/accounts/1")
                .header("Authorization", "Bearer " + employeeToken)
                .with(csrf())
                .with(user(authEmployee))
                .contentType("application/json")
                .content(request.toString())
            ).andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.iban").value("NL01INHO0123456789"))
            .andExpect(jsonPath("$.accountType").value(AccountType.CHECKING.getValue()))
            .andExpect(jsonPath("$.balance").value(1000.0))
            .andExpect(jsonPath("$.active").value(true))
            .andExpect(jsonPath("$.absoluteLimit").value(100.0));
    }

    @Test
    void patchAccountWithNonExistingAccountWillGiveStatus404() throws Exception {
        // create AtmRequest
        JSONObject request = new JSONObject();
        request.put("absoluteLimit", 100.0);
        AccountPatchRequest accountPatchRequest = AccountPatchRequest.builder()
            .absoluteLimit(100.0)
            .build();
        // mock the service
        when(accountService.updateAccount(1L, accountPatchRequest)).thenThrow(new AccountNotFoundException("Account with id: 1 not found"));


        // call the controller
        mockMvc.perform(patch("/accounts/1")
                .header("Authorization", "Bearer " + employeeToken)
                .with(csrf())
                .with(user(authEmployee))
                .contentType("application/json")
                .content(request.toString())
            ).andDo(print())
            .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    @Test
    void patchAccountWithCustomerWillThrow401() throws Exception {
        // create account for the authUser
        Account account = Account.builder()
            .id(1)
            .iban("NL01INHO0123456789")
            .accountType(AccountType.CHECKING)
            .balance(1000.0)
            .user(authCustomer)
            .isActive(true)
            .absoluteLimit(0.0)
            .createdAt(LocalDateTime.of(2023, 1, 1, 0, 0, 0))
            .build();

        AccountPatchRequest accountPatchRequest = AccountPatchRequest.builder()
            .absoluteLimit(100.0)
            .build();

        // create AtmRequest
        JSONObject request = new JSONObject();
        request.put("absoluteLimit", 100.0);

        // create the TransactionResponse that the service should return
        AccountResponse accountResponse = AccountResponse.builder()
            .id(1)
            .iban("NL01INHO0123456789")
            .accountType(AccountType.CHECKING.getValue())
            .balance(1000.0)
            .active(true)
            .absoluteLimit(100.0)
            .build();

        // mock the service
        when(accountService.updateAccount(1L, accountPatchRequest)).thenReturn(accountResponse);


        authCustomer = new User(2, "user2@example.com", 456123788, "Jim", "John", passwordEncoder.encode("1234"), "0987654321", RoleType.CUSTOMER, LocalDate.now().minusYears(30), LocalDateTime.now(), 5000, 200, false, null);

        // call the controller
        mockMvc.perform(patch("/accounts/1")
                .header("Authorization", "Bearer " + customerToken)
                .with(csrf())
                .with(user(authCustomer))
                .contentType("application/json")
                .content(request.toString())
            ).andDo(print())
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.message").value("Access Denied"));
    }

    @Test
    void patchAccountButBodyIsEmptyThrows400() throws Exception {
        mockMvc.perform(patch("/accounts/1")
                .header("Authorization", "Bearer " + employeeToken)
                .with(csrf())
                .with(user(authEmployee))
                .contentType("application/json")
            ).andDo(print())
            .andExpect(status().isBadRequest());
    }


    @Test
    void depositWithInsufficientFundsThrows400() throws Exception {
        // create AtmRequest
        JSONObject request = new JSONObject();
        request.put("amount", 100.0);

        // this error message: "Sender has insufficient funds"
        when(accountService.deposit(1L, 100.0 , authCustomer)).thenThrow(new InsufficientFundsException("Sender has insufficient funds"));

        // call the controller and force spring security
        mockMvc.perform(post("/accounts/1/deposit")
                .header("Authorization", "Bearer " + customerToken)
                .with(csrf())
                .with(user(authCustomer))
                .contentType("application/json")
                .content(request.toString())
            ).andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Sender has insufficient funds"));
    }

    @Test
    void depositWithDayLimitExceededThrows400() throws Exception {
        // create AtmRequest
        JSONObject request = new JSONObject();
        request.put("amount", 100.0);

        // this error message: "Sender has insufficient funds"
        when(accountService.deposit(1L, 100.0 , authCustomer)).thenThrow(new TransactionFailedException("Day limit exceeded"));

        // call the controller
        mockMvc.perform(post("/accounts/1/deposit")
                .header("Authorization", "Bearer " + customerToken)
                .with(csrf())
                .with(user(authCustomer))
                .contentType("application/json")
                .content(request.toString())
            ).andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Day limit exceeded"));
    }
}