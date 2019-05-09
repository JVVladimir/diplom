package ru.hse.business.entity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class ResponseData extends Data implements Serializable {

    private final byte command;

    public ResponseData(byte command, short[] vector, short out) {
        super(vector, out);
        this.command = command;
    }

    public ResponseData(byte command) {
        super(null, (short) 0);
        this.command = command;
    }

    public byte getCommand() {
        return command;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResponseData that = (ResponseData) o;
        return command == that.command;
    }

    @Override
    public int hashCode() {
        return Objects.hash(command);
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
