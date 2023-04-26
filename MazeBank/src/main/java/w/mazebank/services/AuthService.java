package w.mazebank.services;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import w.mazebank.enums.RoleType;
import w.mazebank.models.User;
import w.mazebank.models.requests.LoginRequest;
import w.mazebank.models.requests.RegisterRequest;
import w.mazebank.models.responses.AuthenticationResponse;
import w.mazebank.repositories.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    ModelMapper modelMapper = new ModelMapper();

    public AuthenticationResponse register(RegisterRequest request) {
        //        User user1 = new User(3, "user3@example.com", 123456785, "Sebas", "Doe", passwordEncoder.encode("1234"), "1234567890", RoleType.CUSTOMER, LocalDate.now().minusYears(25), LocalDateTime.now(), false, null);

        //        use modelMapper to map the request to the user object
        User user = modelMapper.map(request, User.class);
        user.setId(3);
        user.setRole(RoleType.CUSTOMER);
        user.setCreatedAt(LocalDateTime.now());
        System.out.println(user);

        // save the user
        userRepository.save(user);

        // generate a token
        String jwt = jwtService.generateToken(user);

        // return the token
        return AuthenticationResponse.builder()
            .token(jwt)
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
        return AuthenticationResponse.builder()
            .token(jwt)
            .build();
    }
}