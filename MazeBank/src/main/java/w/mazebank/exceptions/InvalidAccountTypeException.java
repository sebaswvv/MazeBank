package w.mazebank.exceptions;

public class InvalidAccountTypeException extends NotFoundException {
    public InvalidAccountTypeException(String message) {
        super(message);
    }
}
