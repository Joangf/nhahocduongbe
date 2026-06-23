package vn.viettel.bvrhm.nhahocduong.api.auth.internal.constants.enums;

public enum Status {
    SUCCESS(true),
    FAILED(false);
    private final boolean value;
    Status(boolean value) {
        this.value = value;
    }
    public boolean getValue() {
        return value;
    }
}
