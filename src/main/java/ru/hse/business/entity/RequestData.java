package ru.hse.business.entity;

import java.util.Arrays;
import java.util.Objects;

public class RequestData extends Data {

    private final int resultCode;
    private final int memory;
    private final short[] weight;

    private static final int OK_CODE = 100;

    public RequestData(short[] vector, short out, int resultCode, int memory, short[] weight) {
        super(vector, out);
        this.resultCode = resultCode;
        this.memory = memory;
        this.weight = weight;
    }

    public short[] getWeight() {
        return weight;
    }

    public int getResultCode() {
        return resultCode;
    }

    public int getMemory() {
        return memory;
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
                memory == that.memory &&
                Arrays.equals(weight, that.weight);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(resultCode, memory);
        result = 31 * result + Arrays.hashCode(weight);
        return result;
    }

    @Override
    public String toString() {
        return "RequestData{" +
                "resultCode=" + resultCode +
                ", memory=" + memory +
                ", weight=" + Arrays.toString(weight) +
                ", vector=" + Arrays.toString(vector) +
                ", out=" + out +
                '}';
    }
}
