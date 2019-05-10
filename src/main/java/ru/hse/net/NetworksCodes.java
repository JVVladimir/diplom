package ru.hse.net;

public enum NetworksCodes {
    EXIT(-5, ""),
    SUBMIT_EXIT(-10, ""),
    SAME_PROGRAM(1000, ""),
    SAME_PROGRAM_SUBMIT(1001, "");

    private int code;
    private String description;

    NetworksCodes(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
