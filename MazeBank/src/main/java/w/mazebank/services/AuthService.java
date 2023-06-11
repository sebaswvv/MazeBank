package w.mazebank.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import w.mazebank.exceptions.*;
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

    @Autowired
    private UserServiceJpa userServiceJpa;

    public boolean checkIfUserIsBlocked(String email) throws UserNotFoundException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new BadCredentialsException("User not found"));
        return user.isBlocked();
    }

    public AuthenticationResponse register(RegisterRequest request) throws BsnAlreadyUsedException, UserNotOldEnoughException, EmailAlreadyUsedException {
        // check the request and create a user
        checkRegisterRequest(request);
        User user = buildUser(request);

        // save the user to the db
        userRepository.save(user);
        return buildAuthenticationResponse(user);
    }

    private AuthenticationResponse buildAuthenticationResponse(User user) {
        String jwt = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
            .authenticationToken(jwt)
            .build();
    }

    private User buildUser(RegisterRequest request) {
        return User.builder()
            .email(request.getEmail())
            .bsn(request.getBsn())
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .password(passwordEncoder.encode(request.getPassword()))
            .phoneNumber(request.getPhoneNumber())
            .dateOfBirth(request.getDateOfBirth())
            .build();
    }

    public AuthenticationResponse login(LoginRequest request) throws UserNotFoundException {
        // check if user is blocked
        if (checkIfUserIsBlocked(request.getEmail())) {
            throw new UnauthorizedAccountAccessException("User is blocked");
        }

        // authenticate user
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );

        // get the user
        User user = userServiceJpa.getUserByEmail(request.getEmail());
        return buildAuthenticationResponse(user);
    }

    private void checkRegisterRequest(RegisterRequest request) throws UserNotOldEnoughException, BsnAlreadyUsedException, EmailAlreadyUsedException {
        checkIfEmailAlreadyUsed(request);
        checkIfBsnAlreadyUsed(request);
        checkIfUserIs18(request);
    }

    private static void checkIfUserIs18(RegisterRequest request) throws UserNotOldEnoughException {
        if (request.getDateOfBirth().plusYears(18).isAfter(java.time.LocalDate.now())) {
            throw new UserNotOldEnoughException("User is not 18 years or older");
        }
    }

    private void checkIfBsnAlreadyUsed(RegisterRequest request) throws BsnAlreadyUsedException {
        if (userRepository.findByBsn(request.getBsn()).isPresent()) {
            throw new BsnAlreadyUsedException("BSN already in use");
        }
    }

    private void checkIfEmailAlreadyUsed(RegisterRequest request) throws EmailAlreadyUsedException {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyUsedException("Email already in use");
        }
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
}