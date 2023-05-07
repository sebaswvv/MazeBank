package w.mazebank.exceptions;

public class DisallowedFieldException extends IllegalArgumentException {
    public DisallowedFieldException(String message) {
        super(message);
    }
}