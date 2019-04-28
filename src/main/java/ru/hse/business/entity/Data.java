package ru.hse.business.entity;

public abstract class Data {

    final short[] vector;
    final short out;

    public Data(short[] vector, short out) {
        this.vector = vector;
        this.out = out;
    }

    public short[] getVector() {
        return vector;
    }

    public short getOut() {
        return out;
    }
}
