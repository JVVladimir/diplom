package ru.hse.business.entity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class RequestData extends Data implements Serializable {

    private final int resultCode;
    private final int mem;
    private final short[] w;

    private static final int OK_CODE = 100;

    public RequestData(short[] in, short out, int resultCode, int mem, short[] w) {
        super(in, out);
        this.resultCode = resultCode;
        this.mem = mem;
        this.w = w;
    }

    public RequestData(int resultCode) {
        super(null, (short) 0);
        this.resultCode = resultCode;
        this.mem = 0;
        this.w = null;
    }

    public short[] getWeight() {
        return w;
    }

    public int getResultCode() {
        return resultCode;
    }

    public int getMemory() {
        return mem;
    }

    public boolean isOk() {
        return resultCode == OK_CODE;
    }

    public boolean vecHasLen(int len) {
        return in.length == len;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestData that = (RequestData) o;
        return resultCode == that.resultCode &&
                mem == that.mem &&
                Arrays.equals(w, that.w);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(resultCode, mem);
        result = 31 * result + Arrays.hashCode(w);
        return result;
    }

    @Override
    public String toString() {
        return "RequestData{" +
                "resultCode=" + resultCode +
                ", mem=" + mem +
                ", w=" + Arrays.toString(w) +
                ", in=" + Arrays.toString(in) +
                ", out=" + out +
                '}';
    }
}
