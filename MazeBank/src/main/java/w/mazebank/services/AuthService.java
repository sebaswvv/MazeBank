package w.mazebank.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import w.mazebank.models.User;
import w.mazebank.models.requests.LoginRequest;
import w.mazebank.models.requests.RefreshRequest;
import w.mazebank.models.requests.RegisterRequest;
import w.mazebank.models.responses.AuthenticationResponse;
import w.mazebank.models.responses.RefreshResponse;
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

    public RefreshResponse refresh(RefreshRequest refreshToken) {
        return jwtService.refreshJwtToken(refreshToken);
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
        String refreshToken = jwtService.generateRefreshToken(user);

        // return the token
        return AuthenticationResponse.builder()
            .authenticationToken(jwt)
            .refreshToken(refreshToken)
            .build();
    }

    public AuthenticationResponse login(LoginRequest request) {
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
        String refreshToken = jwtService.generateRefreshToken(user);
        return AuthenticationResponse.builder()
            .authenticationToken(jwt)
            .refreshToken(refreshToken)
            .build();
    }
}