package w.mazebank.controllers;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import w.mazebank.exceptions.BsnAlreadyUsedException;
import w.mazebank.exceptions.EmailAlreadyUsedException;
import w.mazebank.exceptions.UserNotOldEnoughException;
import w.mazebank.models.requests.RegisterRequest;
import w.mazebank.models.responses.AuthenticationResponse;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends BaseControllerTest{
    @Test
    void registerReturns201() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
            .email("user4@example.com")
            .bsn(456123759)
            .firstName("John")
            .lastName("Doe")
            .password("Password123!")
            .phoneNumber("0612345678")
            .dateOfBirth(LocalDate.of(2000, 1, 1))
            .build();

        // use info from above to create a request
        JSONObject request = new JSONObject();
        request.put("email", "user4@example.com");
        request.put("bsn", 456123759);
        request.put("firstName", "John");
        request.put("lastName", "Doe");
        request.put("password", "Password123!");
        request.put("phoneNumber", "0612345678");
        request.put("dateOfBirth", "2000-01-01");

        AuthenticationResponse response = AuthenticationResponse.builder()
            .authenticationToken("token")
            .build();

        when(authService.register(registerRequest)).thenReturn(response);

        // call the controller
        mockMvc.perform(post("/auth/register")
                .with(csrf())
                .contentType("application/json")
                .content(request.toString())
            ).andDo(print())
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString()
            .contains("authenticationToken");
    }

    @Test
    void registerMissingAFieldsReturns400() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
            .email("")
            .bsn(456123759)
            .firstName("John")
            .lastName("Doe")
            .password("Password123!")
            .phoneNumber("0612345678")
            .dateOfBirth(LocalDate.of(2000, 1, 1))
            .build();

        // use info from above to create a request
        JSONObject request = new JSONObject();
        request.put("email", "");
        request.put("bsn", 456123759);
        request.put("firstName", "John");
        request.put("lastName", "Doe");
        request.put("password", "Password123!");
        request.put("phoneNumber", "0612345678");
        request.put("dateOfBirth", "2000-01-01");

        when(authService.register(registerRequest)).thenReturn(null);

        // call the controller
        mockMvc.perform(post("/auth/register")
                .with(csrf())
                .contentType("application/json")
                .content(request.toString())
            ).andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    void registerWithExistingBsnReturns400() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
            .email("user4@example.com")
            .bsn(456123759)
            .firstName("John")
            .lastName("Doe")
            .password("Password123!")
            .phoneNumber("0612345678")
            .dateOfBirth(LocalDate.of(2000, 1, 1))
            .build();

        // use info from above to create a request
        JSONObject request = new JSONObject();
        request.put("email", "user4@example.com");
        request.put("bsn", 456123759);
        request.put("firstName", "John");
        request.put("lastName", "Doe");
        request.put("password", "Password123!");
        request.put("phoneNumber", "0612345678");
        request.put("dateOfBirth", "2000-01-01");

        when(authService.register(registerRequest)).thenThrow(new BsnAlreadyUsedException("BSN already in use"));

        // use info from above to create a request
        mockMvc.perform(post("/auth/register")
                .with(csrf())
                .contentType("application/json")
                .content(request.toString())
            ).andDo(print())
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse()
            .getContentAsString()
            .contains("BSN already in use");
    }



}