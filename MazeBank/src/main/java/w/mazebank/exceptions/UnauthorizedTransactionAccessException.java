package w.mazebank.exceptions;

public class UnauthorizedTransactionAccessException extends RuntimeException {
    public UnauthorizedTransactionAccessException(String message) {
        super(message);
    }
}
