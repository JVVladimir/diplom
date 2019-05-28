package ru.hse.business.entity;

import java.io.Serializable;

public abstract class Data implements Serializable {

    final short[] in;
    final short out;

    public Data(short[] in, short out) {
        this.in = in;
        this.out = out;
    }

    public short[] getInput() {
        return in;
    }

    public short getOut() {
        return out;
    }
}
