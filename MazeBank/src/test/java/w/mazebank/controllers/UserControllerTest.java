package w.mazebank.controllers;

import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import w.mazebank.enums.RoleType;
import w.mazebank.exceptions.UnauthorizedUserAccessException;
import w.mazebank.exceptions.UserNotFoundException;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Test
    void getUserByIdShouldReturnStatus401IfUnauthorized() throws Exception {
        when(jwtService.extractEmail(Mockito.anyString())).thenReturn("user1@example.com");
        when(jwtService.isTokenValid(Mockito.anyString(), Mockito.any())).thenThrow(new SignatureException("Unauthorized"));

        mockMvc.perform(get("/users/1")
                .header("Authorization", "Bearer invalid-token")
            ).andDo(print())
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value("Unauthorized"));
    }

    @Test
    void getUserByIdShouldReturnStatus403IfForbidden() throws Exception {
        when(userServiceJpa.getUserByIdAndValidate(Mockito.anyLong(), Mockito.any())).thenThrow(new UnauthorizedUserAccessException("User not allowed to access user with id: 3"));

        // Expect 403, because the user is not an employee or admin. And expect a message: "Forbidden"
        mockMvc.perform(get("/users/3")
                .header("Authorization", "Bearer " + customerToken)
                .with(user(authCustomer))
            ).andDo(print())
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.message").value("User not allowed to access user with id: 3"));
    }

    @Test
    void getUserByIdShouldReturnStatus200IfEmployee() throws Exception {
        when(userServiceJpa.getUserByIdAndValidate(Mockito.anyLong(), Mockito.any())).thenReturn(authCustomer);

        mockMvc.perform(get("/users/1")
                .header("Authorization", "Bearer " + employeeToken)
                .with(user(authEmployee))
            ).andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.firstName").value("John"))
            .andExpect(jsonPath("$.lastName").value("Doe"))
            .andExpect(jsonPath("$.role").value(RoleType.CUSTOMER.toString()))
            .andExpect(jsonPath("$.blocked").value(false))
            .andExpect(jsonPath("$.createdAt").exists());
    }
}