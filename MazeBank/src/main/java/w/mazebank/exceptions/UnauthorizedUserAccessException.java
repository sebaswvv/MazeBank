package w.mazebank.exceptions;

public class UnauthorizedUserAccessException extends RuntimeException {
    public UnauthorizedUserAccessException(String message) {
        super(message);
    }
}
