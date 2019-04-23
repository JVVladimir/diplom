package ru.hse.business.entity;

import java.util.Arrays;
import java.util.Objects;

public class RequestData extends Data {

    private int resultCode;
    private int memory;
    private short[] weight;
    private char[] message;
    private short length;

    private static final int OK_CODE = 100;

    public RequestData(int resultCode, char[] plainMessage, short length) {
        this.resultCode = resultCode;
        this.message = plainMessage;
        this.length = length;
    }

    public RequestData(int resultCode, short[] vector, short out, int memory) {
        this.resultCode = resultCode;
        this.vector = vector;
        this.out = out;
        this.memory = memory;
    }

    public short[] getWeight() {
        return weight;
    }

    public void setWeight(short[] weight) {
        this.weight = weight;
    }

    public RequestData(int resultCode) {
        this.resultCode = resultCode;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
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

    public boolean isOk() {
        return resultCode == OK_CODE;
    }

    public boolean vecHasLen(int len) {
        return vector.length == len;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestData that = (RequestData) o;
        return resultCode == that.resultCode &&
                memory == that.memory;
    }

    @Override
    public int hashCode() {
        return Objects.hash(resultCode, memory);
    }

    @Override
    public String toString() {
        return "RequestData{" +
                "resultCode=" + resultCode +
                ", memory=" + memory +
                ", weight=" + Arrays.toString(weight) +
                ", message=" + Arrays.toString(message) +
                ", length=" + length +
                ", vector=" + Arrays.toString(vector) +
                ", out=" + out +
                '}';
    }
}
