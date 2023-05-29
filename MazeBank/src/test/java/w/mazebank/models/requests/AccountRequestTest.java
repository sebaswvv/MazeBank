package w.mazebank.models.requests;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void accountTypeCannotBeNull() {
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setAccountType(null);

        // Validate
        assertFalse(validator.validate(accountRequest).isEmpty());
    }

    @Test
    void userIdCannotBeNull() {
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setUserId(0);

        // Validate
        assertFalse(validator.validate(accountRequest).isEmpty());
    }

    @Test
    void isActiveCannotBeNull() {
        AccountRequest accountRequest = new AccountRequest();

        // Validate
        assertFalse(validator.validate(accountRequest).isEmpty());
    }

    @Test
    void absoluteLimitCannotBeNull() {
        AccountRequest accountRequest = new AccountRequest();

        // Validate
        assertFalse(validator.validate(accountRequest).isEmpty());
    }

    @Test
    void absoluteLimitCannotBeLessThan0() {
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setAbsoluteLimit(-1);

        // Validate
        assertFalse(validator.validate(accountRequest).isEmpty());
    }

}