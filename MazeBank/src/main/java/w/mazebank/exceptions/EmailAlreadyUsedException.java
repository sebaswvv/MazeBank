package w.mazebank.exceptions;

public class EmailAlreadyUsedException extends NotFoundException {
    public EmailAlreadyUsedException(String message) {
        super(message);
    }
}