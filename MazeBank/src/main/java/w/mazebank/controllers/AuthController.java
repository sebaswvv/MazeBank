package w.mazebank.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import w.mazebank.exceptions.BsnAlreadyUsedException;
import w.mazebank.exceptions.EmailAlreadyUsedException;
import w.mazebank.exceptions.UserNotOldEnoughException;
import w.mazebank.models.requests.LoginRequest;
import w.mazebank.models.requests.RegisterRequest;
import w.mazebank.models.responses.AuthenticationResponse;
import w.mazebank.services.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
        @RequestBody @Valid RegisterRequest request
    ) throws BsnAlreadyUsedException, UserNotOldEnoughException, EmailAlreadyUsedException {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
        @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }
}