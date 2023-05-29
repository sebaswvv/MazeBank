package w.mazebank.exceptions;

public class BsnAlreadyUsedException extends NotFoundException {
    public BsnAlreadyUsedException(String message) {
        super(message);
    }
}