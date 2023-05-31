package w.mazebank.models.requests;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class AccountPatchRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testAbsoluteLimit() {
        AccountPatchRequest accountPatchRequest = new AccountPatchRequest();
        accountPatchRequest.setAbsoluteLimit(-1.0);
        Set<ConstraintViolation<AccountPatchRequest>> violations = validator.validate(accountPatchRequest);
        assertEquals(1, violations.size());
        assertEquals("Absolute limit must be greater than 0", violations.iterator().next().getMessage());
    }
}
