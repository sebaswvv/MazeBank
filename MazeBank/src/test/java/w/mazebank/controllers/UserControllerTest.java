package w.mazebank.controllers;

import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import w.mazebank.enums.RoleType;
import w.mazebank.exceptions.AccountNotFoundException;
import w.mazebank.exceptions.UnauthorizedUserAccessException;
import w.mazebank.exceptions.UserHasAccountsException;
import w.mazebank.exceptions.UserNotFoundException;
import w.mazebank.models.User;
import w.mazebank.models.requests.UserPatchRequest;
import w.mazebank.models.responses.UserResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    // delete user by id
    @Test
    void deleteUserByIdShouldGive200IfUserWasDeleteByEmployee() throws Exception {
        mockMvc.perform(delete("/users/1")
                .header("Authorization", "Bearer " + employeeToken)
                .with(csrf())
                .with(user(authEmployee))
            ).andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("User with id: 1 was deleted successfully"));
    }

    @Test
    void deleteUserByIdShouldThrow404IfUserIsNotFound() throws Exception {
        doThrow(new UserNotFoundException("User not found with id: " + 1))
            .when(userServiceJpa).deleteUserById(1L);

        mockMvc.perform(delete("/users/1")
                .header("Authorization", "Bearer " + employeeToken)
                .with(user(authEmployee))
            ).andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("User not found with id: 1"));
    }

    @Test
    void canDeleteUserIfUserHasAccounts() throws Exception {
        doThrow(new UserHasAccountsException("user has accounts, cannot delete user"))
            .when(userServiceJpa).deleteUserById(1L);

        mockMvc.perform(delete("/users/1")
                .header("Authorization", "Bearer " + employeeToken)
                .with(user(authEmployee))
            ).andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("user has accounts, cannot delete user"));
    }

    @Test
    void deleteUserByIdCannotBeDoneByCustomer() throws Exception {
        mockMvc.perform(delete("/users/1")
                .header("Authorization", "Bearer " + customerToken)
                .with(user(authCustomer))
            ).andDo(print())
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.message").value("Access Denied"));
    }

    // patch user by id
    @Test
    void patchUserByIdShouldReturn200IfUserWasUpdated() throws Exception {
        UserPatchRequest userPatchRequest = new UserPatchRequest();
        userPatchRequest.setFirstName("Jane");
        userPatchRequest.setLastName("Doe");
        userPatchRequest.setDayLimit(500.00);

        User patchedUser = new User();
        patchedUser.setFirstName("Jane");
        patchedUser.setLastName("Doe");
        patchedUser.setDayLimit(500.00);

        when(userServiceJpa.patchUserById(1L, userPatchRequest, authCustomer)).thenReturn(patchedUser);

        mockMvc.perform(patch("/users/1")
                .header("Authorization", "Bearer " + customerToken)
                .with(csrf())
                .with(user(authCustomer))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userPatchRequest))
            ).andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("Jane"))
            .andExpect(jsonPath("$.lastName").value("Doe"))
            .andExpect(jsonPath("$.dayLimit").value(500.00));
    }

    @Test
    void patchUserByIdShouldReturn404IfUserIsNotFound() throws Exception {
        UserPatchRequest userPatchRequest = new UserPatchRequest();
        userPatchRequest.setFirstName("Jane");
        userPatchRequest.setLastName("Doe");
        userPatchRequest.setDayLimit(500.00);

        when(userServiceJpa.patchUserById(1L, userPatchRequest, authCustomer))
            .thenThrow(new UserNotFoundException("User not found with id: " + 1));

        mockMvc.perform(patch("/users/1")
                .header("Authorization", "Bearer " + customerToken)
                .with(user(authCustomer))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userPatchRequest))
            ).andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("User not found with id: 1"));
    }

    @Test
    void patchUserByIdShouldReturn403IfUserIsNotEmployeeAndNotUserPerforming() throws Exception {
        UserPatchRequest userPatchRequest = new UserPatchRequest();
        userPatchRequest.setFirstName("Jane");
        userPatchRequest.setLastName("Doe");
        userPatchRequest.setDayLimit(500.00);

        when(userServiceJpa.patchUserById(2L, userPatchRequest, authCustomer))
            .thenThrow(new UnauthorizedUserAccessException("Access Denied"));

        mockMvc.perform(patch("/users/2")
                .header("Authorization", "Bearer " + customerToken)
                .with(user(authCustomer))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userPatchRequest))
            ).andDo(print())
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.message").value("Access Denied"));
    }

    // user can be patched if user is employee
    @Test
    void patchUserByIdShouldReturn200IfUserIsEmployee() throws Exception {
        UserPatchRequest userPatchRequest = new UserPatchRequest();
        userPatchRequest.setFirstName("Jane");
        userPatchRequest.setLastName("Doe");
        userPatchRequest.setDayLimit(500.00);

        User patchedUser = new User();
        patchedUser.setFirstName("Jane");
        patchedUser.setLastName("Doe");
        patchedUser.setDayLimit(500.00);

        when(userServiceJpa.patchUserById(1L, userPatchRequest, authEmployee)).thenReturn(patchedUser);

        mockMvc.perform(patch("/users/1")
                .header("Authorization", "Bearer " + employeeToken)
                .with(csrf())
                .with(user(authEmployee))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userPatchRequest))
            ).andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("Jane"))
            .andExpect(jsonPath("$.lastName").value("Doe"))
            .andExpect(jsonPath("$.dayLimit").value(500.00));
    }
}