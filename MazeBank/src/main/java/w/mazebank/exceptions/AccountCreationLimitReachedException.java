package w.mazebank.exceptions;

public class AccountCreationLimitReachedException extends BadRequestException {
    public AccountCreationLimitReachedException(String message) {
        super(message);
    }
}
