package w.mazebank.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
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
import w.mazebank.enums.RoleType;
import w.mazebank.exceptions.UserNotFoundException;
import w.mazebank.models.User;
import w.mazebank.repositories.UserRepository;
import w.mazebank.services.AuthService;
import w.mazebank.services.JwtService;
import w.mazebank.services.UserServiceJpa;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest({UserController.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserServiceJpa userServiceJpa;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private User authUser;
    private User otherUser;
    private String token;

    @BeforeAll
    void setUp() throws UserNotFoundException {
        authUser = new User(3, "user3@example.com", 456123789, "Jim", "John", passwordEncoder.encode("1234"), "0987654321", RoleType.EMPLOYEE, LocalDate.now().minusYears(30), LocalDateTime.now(), 5000, 200, false, null);
        otherUser = new User(4, "user59@example.com", 456123789, "Jim", "John", passwordEncoder.encode("1234"), "0987654321", RoleType.CUSTOMER, LocalDate.now().minusYears(30), LocalDateTime.now(), 5000, 200, false, null);

        when(userRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(authUser));
        when(userServiceJpa.getUserById(Mockito.anyLong())).thenReturn(authUser);

        token = new JwtService().generateToken(authUser);
    }

    @Test
    void getUserByIdShouldReturnStatus200OkAndObject() throws Exception {
        // create user
        User user = User.builder()
            .id(1)
            .firstName("John")
            .lastName("Doe")
            .role(RoleType.CUSTOMER)
            .blocked(false)
            .createdAt(LocalDateTime.now())
            .build();

        when(userServiceJpa.getUserById(Mockito.anyLong())).thenReturn(user);

        // get user
        mockMvc.perform(get("/users/1")
                .header("Authorization", "Bearer " + token)
                .with(user(authUser))
            ).andDo(print())
            .andExpect(status().isOk())
            .andDo(print())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.firstName").value("John"))
            .andExpect(jsonPath("$.lastName").value("Doe"))
            .andExpect(jsonPath("$.role").value(RoleType.CUSTOMER.toString()))
            .andExpect(jsonPath("$.blocked").value(false))
            .andExpect(jsonPath("$.createdAt").exists());
    }

    // 404 not found, dus de gebruiker bestaat niet
    @Test
    void getUserByIdShouldReturnStatus404IfNotFound() throws Exception {
        when(userServiceJpa.getUserById(Mockito.anyLong())).thenThrow(new UserNotFoundException("User not found with id: " + 1));

        // Expect 404, because the user does not exist. And expect a message: "User not found"
        mockMvc.perform(get("/users/1")
                .header("Authorization", "Bearer " + token)
                .with(user(authUser))
            ).andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("User not found with id: 1"));
    }

    // @Test
    // void getUserByIdShouldReturnStatus401IfUnauthorized() throws Exception {
    //     // Expect 401, because the token is not valid. And expect a message: "Unauthorized"
    //     mockMvc.perform(get("/users/1")
    //             .header("Authorization", "Bearer invalid-token")
    //         ).andDo(print())
    //         .andExpect(status().isUnauthorized())
    //         .andExpect(jsonPath("$.message").value("Unauthorized"));
    // }

    // 403 forbidden, dus je mag dit niet doen als de gebruiker die je bent
    // @Test
    // void getUserByIdShouldReturnStatus403IfForbidden() throws Exception {
    //     token = new JwtService().generateToken(otherUser);
    //
    //     when(userRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(otherUser));
    //     when(userServiceJpa.getUserById(Mockito.anyLong())).thenReturn(otherUser);
    //
    //     // Expect 403, because the user is not an employee or admin. And expect a message: "Forbidden"
    //     mockMvc.perform(get("/users/1")
    //             .header("Authorization", "Bearer " + token)
    //             .with(user(otherUser))
    //         ).andDo(print())
    //         .andExpect(status().isForbidden());
    //         // .andExpect(jsonPath("$.message").value("Forbidden"));
    // }
}