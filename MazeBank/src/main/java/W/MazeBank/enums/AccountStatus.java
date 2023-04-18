package W.MazeBank.enums;

public enum AccountStatus {
    ACTIVE(0),
    CLOSED(1);

    private int value;

    AccountStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
