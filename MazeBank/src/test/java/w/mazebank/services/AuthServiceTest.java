package w.mazebank.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import w.mazebank.enums.RoleType;
import w.mazebank.exceptions.*;
import w.mazebank.models.User;
import w.mazebank.models.requests.LoginRequest;
import w.mazebank.models.requests.RegisterRequest;
import w.mazebank.models.responses.AuthenticationResponse;
import w.mazebank.repositories.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthServiceTest {
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserServiceJpa userServiceJpa;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    private RegisterRequest registerRequest;
    private User user;

    @BeforeEach
    void setUp() {
        // setup the passwordEncoded to prevent errors
        authService.setPasswordEncoder(passwordEncoder);

        // create Register Request
        registerRequest = new RegisterRequest("info@mail.nl", 123456789, "John", "Doe", "Abc123!@", "0612345678", LocalDate.of(1990, 1, 1));

        // create user with same info as register request
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
    void register() throws BsnAlreadyUsedException, UserNotOldEnoughException, EmailAlreadyUsedException {
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
    void registerButEmailIsTaken() {
        // mock the userRepository
        when(userRepository.findByEmail(any(String.class))).thenReturn(java.util.Optional.of(user));

        // call the register method
        try {
            authService.register(registerRequest);
        } catch (Exception e) {
            assertEquals("Email already in use", e.getMessage());
        }

        // test results
        verify(userRepository).findByEmail(any(String.class));
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(registerRequest.getPassword());
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    void registerButBsnIsTaken() {
        // mock the userRepository
        when(userRepository.findByBsn(any(Integer.class))).thenReturn(java.util.Optional.of(user));

        // call the register method
        try {
            authService.register(registerRequest);
        } catch (Exception e) {
            assertEquals("BSN already in use", e.getMessage());
        }

        // test results
        verify(userRepository).findByBsn(any(Integer.class));
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(registerRequest.getPassword());
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    void registerButUserIsNotOldEnough() {
        // modify the registerRequest to make the user not old enough
        registerRequest.setDateOfBirth(LocalDate.now().minusYears(17));

        // call the register method
        try {
            authService.register(registerRequest);
        } catch (Exception e) {
            assertEquals("User is not 18 years or older", e.getMessage());
        }

        // test results
        verify(userRepository).findByBsn(any(Integer.class));
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(registerRequest.getPassword());
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    void loginIsSuccessful() throws UserNotFoundException {
        LoginRequest loginRequest = LoginRequest.builder()
            .email(registerRequest.getEmail())
            .password(registerRequest.getPassword())
            .build();

        // mock methods
        when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(user));
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userServiceJpa.getUserByEmail(any())).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("THISISAFAKETOKEN");

        // call the login method
        AuthenticationResponse response = authService.login(loginRequest);

        // test results
        assertEquals("THISISAFAKETOKEN", response.getAuthenticationToken());
    }

    @Test
    void loginFailsWhenUserIsBlocked() {
        user.setBlocked(true);

        LoginRequest loginRequest = LoginRequest.builder()
            .email(registerRequest.getEmail())
            .password(registerRequest.getPassword())
            .build();

        // mock methods
        when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(user));

        // test results
        UnauthorizedAccountAccessException exception = assertThrows(UnauthorizedAccountAccessException.class, () -> authService.login(loginRequest));
        assertEquals("User is blocked", exception.getMessage());
    }
}