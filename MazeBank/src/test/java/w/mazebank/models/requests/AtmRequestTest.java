package w.mazebank.models.requests;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AtmRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void amountIsNegative() {
        AtmRequest atmRequest = new AtmRequest(-0.01);

        // Validate
        Set<ConstraintViolation<AtmRequest>> violations = validator.validate(atmRequest);
        ConstraintViolation<AtmRequest> violation = violations.iterator().next();
        assertFalse(violations.isEmpty());
        assertEquals("Amount should be a positive number", violation.getMessage());
    }

    @Test
    void amountIsNull(){
        AtmRequest atmRequest = new AtmRequest(null);

        // Validate
        Set<ConstraintViolation<AtmRequest>> violations = validator.validate(atmRequest);
        ConstraintViolation<AtmRequest> violation = violations.iterator().next();
        assertFalse(violations.isEmpty());
        assertEquals("Amount is required", violation.getMessage());
    }


}