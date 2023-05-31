package w.mazebank.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
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
class UserControllerTest extends BaseControllerTest {
    @Test
    void getUserByIdShouldReturnStatus200OkAndObject() throws Exception {
        when(userServiceJpa.getUserByIdAndValidate(Mockito.anyLong(), Mockito.any())).thenReturn(authCustomer);

        mockMvc.perform(get("/users/1")
                .header("Authorization", "Bearer " + customerToken)
                .with(user(authCustomer))
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
    
    @Test
    void getUserByIdShouldReturnStatus404IfNotFound() throws Exception {
        when(userServiceJpa.getUserByIdAndValidate(Mockito.anyLong(), Mockito.any())).thenThrow(new UserNotFoundException("User not found with id: " + 1));

        // Expect 404, because the user does not exist. And expect a message: "User not found"
        mockMvc.perform(get("/users/1")
                .header("Authorization", "Bearer " + customerToken)
                .with(user(authCustomer))
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