package w.mazebank.enums;

public enum AccountType {
    SAVINGS(0),
    CHECKING(1);

    private int value;

    AccountType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
