package w.mazebank.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import w.mazebank.enums.AccountType;
import w.mazebank.enums.RoleType;
import w.mazebank.enums.TransactionType;
import w.mazebank.exceptions.TransactionFailedException;
import w.mazebank.exceptions.TransactionNotFoundException;
import w.mazebank.exceptions.UserNotFoundException;
import w.mazebank.models.Account;
import w.mazebank.models.Transaction;
import w.mazebank.models.User;
import w.mazebank.models.requests.TransactionRequest;
import w.mazebank.models.responses.AccountResponse;
import w.mazebank.models.responses.TransactionResponse;
import w.mazebank.repositories.UserRepository;
import w.mazebank.services.AuthService;
import w.mazebank.services.JwtService;
import w.mazebank.services.TransactionServiceJpa;
import w.mazebank.services.UserServiceJpa;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest({TransactionController.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionServiceJpa transactionServiceJpa;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserServiceJpa userServiceJpa;

    @MockBean
    private PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private User authUser;
    private String token;

    @BeforeAll
    void setUp() throws TransactionNotFoundException, UserNotFoundException {
        authUser = new User(3, "user3@example.com", 456123789, "Jim", "John", passwordEncoder.encode("1234"), "0987654321", RoleType.EMPLOYEE, LocalDate.now().minusYears(30), LocalDateTime.now(), 5000, 200, false, null);

        when(userRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(authUser));
        when(userServiceJpa.getUserById(Mockito.anyLong())).thenReturn(authUser);

        token = new JwtService().generateToken(authUser);

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

    }

    @Test
    void transactionPostStatus201CreatedAndReturnsObject() throws Exception {

        Account sender = Account.builder()
            .id(1)
            .accountType(AccountType.CHECKING)
            .balance(100.00)
            .isActive(true)
            .iban("NL01INHO0000000002")
            .absoluteLimit(0.0)
            .build();

        Account receiver = Account.builder()
            .id(2)
            .accountType(AccountType.SAVINGS)
            .balance(0.00)
            .isActive(true)
            .iban("NL01INHO0000000003")
            .absoluteLimit(0.0)
            .build();

        TransactionRequest transactionRequest = TransactionRequest.builder()
            .description("Test transaction")
            .amount(100.00)
            .senderIban(sender.getIban())
            .receiverIban(receiver.getIban())
            .build();

        TransactionResponse transactionResponse = TransactionResponse.builder()
            .id(1L)
            .description("Test transaction")
            .amount(100.00)
            .userPerforming(1L)
            .sender(sender.getIban())
            .receiver(receiver.getIban())
            .type(TransactionType.TRANSFER.toString())
            .timestamp(LocalDateTime.now())
            .build();

         when(transactionServiceJpa.postTransaction(Mockito.any(TransactionRequest.class), Mockito.any(User.class))).thenReturn(transactionResponse);

        // mock mvc
        mockMvc.perform(post("/transactions")
            .header("Authorization", "Bearer " + token)
            .with(csrf())
            .with(user(authUser))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(transactionRequest))

        )
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Test transaction"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.amount").value(100.00))
            .andExpect(MockMvcResultMatchers.jsonPath("$.userPerforming").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.sender").value("NL01INHO0000000002"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.receiver").value("NL01INHO0000000003"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.type").value("TRANSFER"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andReturn();

    }

    @Test
    void transactionFailedStatus400() throws Exception {

            TransactionRequest transactionRequest = TransactionRequest.builder()
                .description("Test transaction")
                .amount(100.00)
                .senderIban("NL01INHO0000000002")
                .receiverIban("NL01INHO0000000003")
                .build();

            when(transactionServiceJpa.postTransaction(Mockito.any(TransactionRequest.class), Mockito.any(User.class))).thenThrow(new TransactionFailedException("Transaction failed"));

            // mock mvc
            mockMvc.perform(post("/transactions")
                .header("Authorization", "Bearer " + token)
                .with(csrf())
                .with(user(authUser))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(transactionRequest))

            )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Transaction failed"))
                .andReturn();

    }

}
