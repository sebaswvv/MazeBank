package w.mazebank.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import w.mazebank.exceptions.UnauthorizedAccountAccessException;
import w.mazebank.models.User;
import w.mazebank.models.requests.LoginRequest;
import w.mazebank.models.requests.RegisterRequest;
import w.mazebank.models.responses.AuthenticationResponse;
import w.mazebank.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public boolean checkIfUserIsBlocked(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return user.isBlocked();
    }

    public AuthenticationResponse register(RegisterRequest request) {
        User user = User.builder()
            .email(request.getEmail())
            .bsn(request.getBsn())
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .password(passwordEncoder.encode(request.getPassword()))
            .phoneNumber(request.getPhoneNumber())
            .dateOfBirth(request.getDateOfBirth())
            .build();

        // save the user
        userRepository.save(user);

        // generate a token
        String jwt = jwtService.generateToken(user);

        // return the token
        return AuthenticationResponse.builder()
            .authenticationToken(jwt)
            .build();
    }

    public AuthenticationResponse login(LoginRequest request) {
        // check if user is blocked
        if (checkIfUserIsBlocked(request.getEmail())) {
            throw new UnauthorizedAccountAccessException("User is blocked");
        }

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );

        // get user
        var user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // generate a token and return response
        String jwt = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
            .authenticationToken(jwt)
            .build();
    }
}