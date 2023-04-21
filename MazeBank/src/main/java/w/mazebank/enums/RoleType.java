package w.mazebank.enums;

public enum RoleType {
    CUSTOMER(0),
    EMPLOYEE(1);

    private int value;

    RoleType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
