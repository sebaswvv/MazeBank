package w.mazebank.controllers;

import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import w.mazebank.enums.RoleType;
import w.mazebank.exceptions.AccountNotFoundException;
import w.mazebank.exceptions.UnauthorizedUserAccessException;
import w.mazebank.exceptions.UserNotFoundException;
import w.mazebank.models.User;
import w.mazebank.models.responses.UserResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends BaseControllerTest {
    // get user by id
    @Test
    void getUserByIdShouldReturnStatus200OkAndObject() throws Exception {
        when(userServiceJpa.getUserByIdAndValidate(Mockito.anyLong(), Mockito.any())).thenReturn(authCustomer);

        mockMvc.perform(get("/users/1")
                .header("Authorization", "Bearer " + customerToken)
                .with(csrf())
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
                .with(csrf())
                .with(user(authCustomer))
            ).andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("User not found with id: 1"));
    }

    @Test
    void getUserByIdShouldReturnStatus403IfForbidden() throws Exception {
        when(userServiceJpa.getUserByIdAndValidate(Mockito.anyLong(), Mockito.any())).thenThrow(new UnauthorizedUserAccessException("User not allowed to access user with id: 3"));

        // Expect 403, because the user is not an employee or admin. And expect a message: "Forbidden"
        mockMvc.perform(get("/users/3")
                .header("Authorization", "Bearer " + customerToken)
                .with(csrf())
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
                .with(csrf())
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


    // get all users
    // secured by employee
    @Test
    void getAllUsersShouldReturnStatus200OkAndObject() throws Exception {
        // parse users to user
        when(userServiceJpa.getAllUsers(0, 10, "asc", null)).thenReturn(userResponses);

        mockMvc.perform(get("/users")
                .header("Authorization", "Bearer " + employeeToken)
                .with(csrf())
                .with(user(authEmployee))
            ).andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].firstName").value("John"))
            .andExpect(jsonPath("$[0].lastName").value("Doe"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].firstName").value("Jane"))
            .andExpect(jsonPath("$[1].lastName").value("Doe"));
    }

    @Test
    void getAllUsersShouldReturnStatus200OkAndObjectWithLimit() throws Exception {
        // parse users to user
        when(userServiceJpa.getAllUsers(0, 1, "asc", null)).thenReturn(userResponses.subList(0, 1));

        mockMvc.perform(get("/users?limit=1")
                .header("Authorization", "Bearer " + employeeToken)
                .with(csrf())
                .with(user(authEmployee))
            ).andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].firstName").value("John"))
            .andExpect(jsonPath("$[0].lastName").value("Doe"));
    }

    @Test
    void getAllUsersShouldReturnStatus403IfForbidden() throws Exception {
        when(userServiceJpa.getAllUsers(0, 10, "asc", null)).thenReturn(userResponses);

        mockMvc.perform(get("/users")
                .header("Authorization", "Bearer " + employeeToken)
                .with(csrf())
                .with(user(authCustomer))
            ).andDo(print())
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.message").value("Access Denied"));
    }

    // block & unblock user
    @Test
    void disableUserShouldReturnStatus200IfEmployee() throws Exception {
        mockMvc.perform(put("/users/1/disable")
                .header("Authorization", "Bearer " + employeeToken)
                .with(csrf())
                .with(user(authEmployee))
            ).andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.locked").value(true));
    }

    @Test
    void disableUserShouldReturn404IfUserNotFound() throws Exception {
        doThrow(new UserNotFoundException("User not found with id: " + 1))
            .when(userServiceJpa).blockUser(1L);

        mockMvc.perform(put("/users/1/disable")
                .header("Authorization", "Bearer " + employeeToken)
                .with(user(authEmployee))
            ).andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("User not found with id: 1"));
    }

    @Test
    void disableUserShouldReturn403WhenUserPerformingIsNotACustomer() throws Exception {
        mockMvc.perform(put("/users/1/disable")
                .header("Authorization", "Bearer " + customerToken)
                .with(user(authCustomer))
            ).andDo(print())
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.message").value("Access Denied"));
    }

    @Test
    void enableUserShouldReturnStatus200IfEmployee() throws Exception {
        mockMvc.perform(put("/users/1/enable")
                .header("Authorization", "Bearer " + employeeToken)
                .with(csrf())
                .with(user(authEmployee))
            ).andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.locked").value(false));
    }

    @Test
    void enableUserShouldReturn404IfUserNotFound() throws Exception {
        doThrow(new UserNotFoundException("User not found with id: " + 1))
            .when(userServiceJpa).unblockUser(1L);

        mockMvc.perform(put("/users/1/enable")
                .header("Authorization", "Bearer " + employeeToken)
                .with(user(authEmployee))
            ).andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("User not found with id: 1"));
    }

    @Test
    void enableUserShouldReturn403WhenUserPerformingIsNotACustomer() throws Exception {
        mockMvc.perform(put("/users/1/enable")
                .header("Authorization", "Bearer " + customerToken)
                .with(user(authCustomer))
            ).andDo(print())
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.message").value("Access Denied"));
    }
}