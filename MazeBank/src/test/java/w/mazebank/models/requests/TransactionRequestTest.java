package w.mazebank.models.requests;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransactionRequestTest {
    private Validator validator;

    @BeforeAll
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenAmountIsNegativeExpectException() {
        TransactionRequest transactionRequest = new TransactionRequest(-1, "description", "NL01INHO0000000001", "NL01INHO0000000002");

        Set<ConstraintViolation<TransactionRequest>> violations = validator.validate(transactionRequest);
        assertFalse(violations.isEmpty());

        ConstraintViolation<TransactionRequest> violation = violations.iterator().next();
        assertEquals("Amount should be a positive number", violation.getMessage());
    }

    @Test
    void whenSenderIsBlankExpectException() {
        TransactionRequest transactionRequest = new TransactionRequest(1, "description", null, "NL01INHO0000000002");

        Set<ConstraintViolation<TransactionRequest>> violations = validator.validate(transactionRequest);
        assertFalse(violations.isEmpty());

        ConstraintViolation<TransactionRequest> violation = violations.iterator().next();
        assertEquals("Sender cannot be blank", violation.getMessage());
    }

    @Test
    void whenSenderIsInvalidExpectException() {
        TransactionRequest transactionRequest = new TransactionRequest(1, "description", "BE02ABCDO0000000001", "NL01INHO0000000002");

        Set<ConstraintViolation<TransactionRequest>> violations = validator.validate(transactionRequest);
        assertFalse(violations.isEmpty());

        ConstraintViolation<TransactionRequest> violation = violations.iterator().next();
        assertEquals("Sender IBAN is not valid", violation.getMessage());
    }

    @Test
    void whenReceiverIsBlankExpectException() {
        TransactionRequest transactionRequest = new TransactionRequest(1, "description", "NL01INHO0000000001", null);

        Set<ConstraintViolation<TransactionRequest>> violations = validator.validate(transactionRequest);
        assertFalse(violations.isEmpty());

        ConstraintViolation<TransactionRequest> violation = violations.iterator().next();
        assertEquals("Receiver cannot be blank", violation.getMessage());
    }

    @Test
    void whenReceiverIsInvalidExpectException() {
        TransactionRequest transactionRequest = new TransactionRequest(1, "description", "NL01INHO0000000001", "BE02ABCDO0000000001");

        Set<ConstraintViolation<TransactionRequest>> violations = validator.validate(transactionRequest);
        assertFalse(violations.isEmpty());

        ConstraintViolation<TransactionRequest> violation = violations.iterator().next();
        assertEquals("Receiver IBAN is not valid", violation.getMessage());
    }
}