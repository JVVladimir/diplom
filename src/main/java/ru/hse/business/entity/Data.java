package ru.hse.business.entity;

public abstract class Data {

    short[] vector;
    short out;

    public short[] getVector() {
        return vector;
    }

    public void setVector(short[] vector) {
        this.vector = vector;
    }

    public short getOut() {
        return out;
    }

    public void setOut(short out) {
        this.out = out;
    }
}
