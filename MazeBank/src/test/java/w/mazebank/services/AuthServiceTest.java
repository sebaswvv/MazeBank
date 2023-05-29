package w.mazebank.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import w.mazebank.enums.RoleType;
import w.mazebank.models.User;
import w.mazebank.models.requests.RegisterRequest;
import w.mazebank.models.responses.AuthenticationResponse;
import w.mazebank.repositories.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthServiceTest {


    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    private RegisterRequest registerRequest;
    private User user;
    @BeforeEach
    void setUp() {
        // setup the passwordEncoded to prevent errors
        authService.setPasswordEncoder(passwordEncoder);

        // create Register Request
        registerRequest = new RegisterRequest("info@mail.nl", 123456789, "John", "Doe", "Abc123!@", "0612345678", LocalDate.of(1990, 1, 1));

        // create usr with same info as register request
        user = User.builder()
            .email(registerRequest.getEmail())
            .bsn(registerRequest.getBsn())
            .firstName(registerRequest.getFirstName())
            .lastName(registerRequest.getLastName())
            .password(registerRequest.getPassword())
            .phoneNumber(registerRequest.getPhoneNumber())
            .dateOfBirth(registerRequest.getDateOfBirth())
            .role(RoleType.CUSTOMER)
            .blocked(false)
            .createdAt(LocalDateTime.now())
            .build();
    }

    @Test
    void register() {
        // mock the userRepository
        when(userRepository.save(any(User.class))).thenReturn(user);

        // mock the passwordEncoder
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn(registerRequest.getPassword());
        when(jwtService.generateToken(any(User.class))).thenReturn("THISISAFAKETOKEN");

        // call the register method
        AuthenticationResponse response = authService.register(registerRequest);

        // test results
        assertEquals("THISISAFAKETOKEN", response.getAuthenticationToken());
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(jwtService).generateToken(any(User.class));
    }

    @Test
    void registerButEmailIsTaken(){
        // mock the userRepository
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(java.util.Optional.of(user));



        // test results
        // assertEquals("email is already taken", exception.getMessage());
        // verify(userRepository).existsByEmail(registerRequest.getEmail());
        // verify(userRepository, never()).save(any(User.class));
        // verify(passwordEncoder, never()).encode(registerRequest.getPassword());
        // verify(jwtService, never()).generateToken(any(User.class));
    }
}