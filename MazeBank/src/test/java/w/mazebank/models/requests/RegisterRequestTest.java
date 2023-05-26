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

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void passwordHasNoNumber() {
        // email, bsn, firstName, lastName, password, phoneNumber, dateOfBirth
        RegisterRequest user = new RegisterRequest("info@mail.nl", 123456789, "John", "Doe", "Passwoord", "0612345678", LocalDate.of(1990, 1, 1));

        // Validate the user
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());

        // Check if the error message is correct
        ConstraintViolation<RegisterRequest> violation = violations.iterator().next();
        assertEquals("Password should be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number and one special character", violation.getMessage());
    }


//    @Test
//    void passwordHasSpecialCharacter(){
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            User user = new User();
//            user.setPassword("password");
//            assertEquals("Password must contain at least one special character (!@#$%^&*()_+=-)", user.getPassword());
//        });
//
//    }
//
//    @Test
//    void passwordHasCapitalLetter(){
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            User user = new User();
//            user.setPassword("password");
//            assertEquals("Password must contain at least one capital letter (A-Z)", user.getPassword());
//        });
//
//    }
//
//    @Test
//    void passwordHasLowercaseLetter(){
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            User user = new User();
//            user.setPassword("password");
//            assertEquals("Password must contain at least one lowercase letter (a-z)", user.getPassword());
//        });
//
//    }
//
//    @Test
//    void passwordHasAtleastEightCharacters(){
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            User user = new User();
//            user.setPassword("password");
//            assertEquals("Password must be at least 8 characters long", user.getPassword());
//        });
//
//    }
}