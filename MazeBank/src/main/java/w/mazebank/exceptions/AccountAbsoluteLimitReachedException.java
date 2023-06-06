package w.mazebank.exceptions;

public class AccountAbsoluteLimitReachedException extends TransactionFailedException {
    public AccountAbsoluteLimitReachedException(String message) {
        super(message);
    }
}
