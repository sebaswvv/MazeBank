package w.mazebank.exceptions;

public class TransactionNotFoundException extends NotFoundException {
    public TransactionNotFoundException(String message) {
        super(message);
    }
}