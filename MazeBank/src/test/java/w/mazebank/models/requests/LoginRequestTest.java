package w.mazebank.models.requests;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void emailIsNull() {
        // create a LoginResponse object with a null email
        LoginRequest user = new LoginRequest(null, "password");

        // validate the object and check if it has any violations
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());

        // check if the first violation is the one we expect
        ConstraintViolation<LoginRequest> violation = violations.iterator().next();
        assertEquals("Email is mandatory", violation.getMessage());
    }

    @Test
    void emailIsBlank(){
        // create a LoginResponse object with a blank email
        LoginRequest user = new LoginRequest("", "password");

        // validate the object and check if it has any violations
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());

        ConstraintViolation<LoginRequest> violation = violations.iterator().next();
        assertEquals("Email is mandatory", violation.getMessage());
    }

    @Test
    void emailIsNotValid(){
        // create a LoginResponse object with an invalid email
        LoginRequest user = new LoginRequest("email", "password");

        // validate the object and check if it has any violations
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());

        ConstraintViolation<LoginRequest> violation = violations.iterator().next();
        assertEquals("Email should be valid", violation.getMessage());
    }

    @Test
    void passwordIsNull() {
        // create a LoginResponse object with a null password
        LoginRequest user = new LoginRequest("test@mail.com", null);

        // validate the object and check if it has any violations
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());

        // check if the first violation is the one we expect
        ConstraintViolation<LoginRequest> violation = violations.iterator().next();
        assertEquals("Password is mandatory", violation.getMessage());
    }

    @Test
    void passwordIsBlank() {
        // create a LoginResponse object with a blank password
        LoginRequest user = new LoginRequest("test@mail.com", "");

        // validate the object and check if it has any violations
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(user);

        // check if the first violation is the one we expect
        ConstraintViolation<LoginRequest> violation = violations.iterator().next();
        assertEquals("Password is mandatory", violation.getMessage());
    }
}