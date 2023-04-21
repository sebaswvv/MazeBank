package w.mazebank.enums;

public enum TransactionType {
    TRANSFER(0),
    DEPOSIT(1),
    WITHDRAWAL(2);

    private int value;

    TransactionType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
