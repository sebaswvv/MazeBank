package w.mazebank.models.requests;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserPatchRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void phoneNumberNot10Digits() {
        UserPatchRequest userPatchRequest = new UserPatchRequest();
        userPatchRequest.setPhoneNumber("123456789");

        // Validate
        Set<ConstraintViolation<UserPatchRequest>> violations = validator.validate(userPatchRequest);
        ConstraintViolation<UserPatchRequest> violation = violations.iterator().next();
        assertFalse(violations.isEmpty());
        assertEquals("Phone number should be 10 digits", violation.getMessage());
    }
}