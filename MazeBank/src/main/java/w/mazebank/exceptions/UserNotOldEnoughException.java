package w.mazebank.exceptions;

public class UserNotOldEnoughException extends NotFoundException {
    public UserNotOldEnoughException(String message) {
        super(message);
    }
}