package ru.hse;

import java.util.Arrays;

public class TestEntity {

    String sensor;
    int time;
    double[] data;

    @Override
    public String toString() {
        return "TestEntity{" +
                "sensor='" + sensor + '\'' +
                ", time=" + time +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
