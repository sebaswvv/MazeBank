package w.mazebank.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import w.mazebank.configurations.ApplicationConfig;
import w.mazebank.configurations.SecurityConfiguration;
import w.mazebank.enums.RoleType;
import w.mazebank.exceptions.UserNotFoundException;
import w.mazebank.models.User;
import w.mazebank.repositories.TransactionRepository;
import w.mazebank.repositories.UserRepository;
import w.mazebank.services.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.when;

@Import({ApplicationConfig.class, SecurityConfiguration.class })
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {AccountController.class, AuthController.class, UserController.class, TransactionController.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BaseControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    public PasswordEncoder passwordEncoder;

    @MockBean
    protected AccountServiceJpa accountService;

    @MockBean
    protected AuthService authService;

    @MockBean
    protected JwtService jwtService;

    @MockBean
    protected UserRepository userRepository;

    @MockBean
    protected UserServiceJpa userServiceJpa;

    @MockBean
    protected TransactionRepository transactionRepository;

    @MockBean
    protected TransactionServiceJpa transactionServiceJpa;

    protected final ObjectMapper objectMapper = new ObjectMapper();

    protected User authEmployee;
    protected User authCustomer;
    protected String employeeToken;
    protected String customerToken;

    @BeforeEach
    void setUp() throws UserNotFoundException {
        authCustomer = new User(1, "user1@example.com", 123456789, "John", "Doe", passwordEncoder.encode("1234"), "1234567890", RoleType.CUSTOMER, LocalDate.now().minusYears(30), LocalDateTime.now(), 5000, 200, false, null);
        authEmployee = new User(3, "user3@example.com", 456123789, "Jim", "John", passwordEncoder.encode("1234"), "0987654321", RoleType.EMPLOYEE, LocalDate.now().minusYears(30), LocalDateTime.now(), 5000, 200, false, null);

        when(userRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(authEmployee));
        when(userServiceJpa.getUserById(Mockito.anyLong())).thenReturn(authEmployee);

        customerToken = new JwtService().generateToken(authCustomer);
        employeeToken = new JwtService().generateToken(authEmployee);
    }
}