package ru.hse.business.entity;

import java.util.Arrays;
import java.util.Objects;

public class ResponseData extends Data {

    private byte command;

    public ResponseData(byte command, int[] vector, int out) {
        this.command = command;
        this.vector = vector;
        this.out = out;
    }

    public ResponseData(byte command) {
        this.command = command;
    }

    public byte getCommand() {
        return command;
    }

    public void setCommand(byte command) {
        this.command = command;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResponseData responseData = (ResponseData) o;
        return command == responseData.command &&
                out == responseData.out &&
                Arrays.equals(vector, responseData.vector);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(command, out);
        result = 31 * result + Arrays.hashCode(vector);
        return result;
    }

    @Override
    public String toString() {
        return "ResponseData{" +
                "command=" + command +
                ", vector=" + Arrays.toString(vector) +
                ", out=" + out +
                '}';
    }
}
