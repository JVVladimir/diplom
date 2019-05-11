package ru.hse.business.entity;

import java.io.Serializable;

public abstract class Data implements Serializable {

    final short[] input;
    final short out;

    public Data(short[] input, short out) {
        this.input = input;
        this.out = out;
    }

    public short[] getInput() {
        return input;
    }

    public short getOut() {
        return out;
    }
}
