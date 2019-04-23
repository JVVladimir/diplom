package ru.hse.business.entity;

import java.util.Arrays;
import java.util.Objects;

public class ResponseData extends Data {

    private byte command;
    private char[] message;
    private short length;

    public ResponseData(byte command, char[] message, short length) {
        this.command = command;
        this.message = message;
        this.length = length;
    }

    public ResponseData(byte command, short[] vector, short out) {
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

    public char[] getMessage() {
        return message;
    }

    public void setMessage(char[] message) {
        this.message = message;
    }

    public short getLength() {
        return length;
    }

    public void setLength(short length) {
        this.length = length;
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
                ", message=" + Arrays.toString(message) +
                ", length=" + length +
                ", vector=" + Arrays.toString(vector) +
                ", out=" + out +
                '}';
    }
}
