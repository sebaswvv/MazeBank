package w.mazebank.models.requests;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class RegisterRequestTest {

    private Validator validator;
    private RegisterRequest user;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        // create Register Request
        user = new RegisterRequest("info@mail.nl", 123456789, "John", "Doe", "Abc123!@", "0612345678", LocalDate.of(1990, 1, 1));
    }

    @Test
    void passwordHasNoDigit() {
        user.setPassword("Abcccc!@");

        // Validate
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        ConstraintViolation<RegisterRequest> violation = violations.iterator().next();
        assertEquals("Password should be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number and one special character", violation.getMessage());
    }

    @Test
    void passwordHasNoLowercaseLetter() {
        user.setPassword("ABC123!@");

        // Validate
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        ConstraintViolation<RegisterRequest> violation = violations.iterator().next();
        assertEquals("Password should be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number and one special character", violation.getMessage());
    }

    @Test
    void passwordHasNoUppercaseLetter() {
        user.setPassword("abc123!@");

        // Validate
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        ConstraintViolation<RegisterRequest> violation = violations.iterator().next();
        assertEquals("Password should be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number and one special character", violation.getMessage());
    }

   @Test
    void passwordIsNot8CharactersLong(){
        user.setPassword("Abc12!@");

        // Validate
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        ConstraintViolation<RegisterRequest> violation = violations.iterator().next();
        assertEquals("Password should be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number and one special character", violation.getMessage());
    }

    @Test
    void passwordHasNoSpecialCharacter(){
        user.setPassword("Abc12345");

        // Validate
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        ConstraintViolation<RegisterRequest> violation = violations.iterator().next();
        assertEquals("Password should be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number and one special character", violation.getMessage());
    }


    // TODO: uitzoeken hoe we dit kunnen testen want het is een integer
    @Test
    void bsnIsNotNull(){
        // Validate
        // Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(user);
        // assertFalse(violations.isEmpty());
        // ConstraintViolation<RegisterRequest> violation = violations.iterator().next();
        // assertEquals("BSN is mandatory", violation.getMessage());
    }

    @Test
    void firstnameIsBlank(){
        user.setFirstName("");

        // Validate
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        ConstraintViolation<RegisterRequest> violation = violations.iterator().next();
        assertEquals("First name is mandatory", violation.getMessage());
    }

    @Test
    void firstnameIsNull(){
        user.setFirstName(null);

        // Validate
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        ConstraintViolation<RegisterRequest> violation = violations.iterator().next();
        assertEquals("First name is mandatory", violation.getMessage());
    }

    @Test
    void lastnameIsBlank(){
        user.setLastName("");

        // Validate
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        ConstraintViolation<RegisterRequest> violation = violations.iterator().next();
        assertEquals("Last name is mandatory", violation.getMessage());
    }

    @Test
    void lastnameIsNull(){
        user.setLastName(null);

        // Validate
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        ConstraintViolation<RegisterRequest> violation = violations.iterator().next();
        assertEquals("Last name is mandatory", violation.getMessage());
    }

    @Test
    void phoneNumberIsNull(){
        user.setPhoneNumber(null);

        // Validate
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        ConstraintViolation<RegisterRequest> violation = violations.iterator().next();
        assertEquals("Phone number is mandatory", violation.getMessage());
    }

    @Test
    void phoneNumberContainsLetter(){
        user.setPhoneNumber("061234567a");

        // Validate
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        ConstraintViolation<RegisterRequest> violation = violations.iterator().next();
        assertEquals("Phone number should be 10 digits", violation.getMessage());
    }

    @Test
    void phoneNumberIsLessThan10Digits(){
        user.setPhoneNumber("061234567");

        // Validate
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        ConstraintViolation<RegisterRequest> violation = violations.iterator().next();
        assertEquals("Phone number should be 10 digits", violation.getMessage());
    }

}