package ru.hse.business.entity;

import java.io.Serializable;

public abstract class Data implements Serializable {

    final short[] vector;
    final short out;

    public Data(short[] vector, short out) {
        this.vector = vector;
        this.out = out;
    }

    public short[] getInput() {
        return vector;
    }

    public short getOut() {
        return out;
    }
}
