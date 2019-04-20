package ru.hse;

import java.util.Arrays;
import java.util.Objects;

public class TestEntity {

    String sensor;
    int time;
    int[] data;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestEntity entity = (TestEntity) o;
        return time == entity.time &&
                Objects.equals(sensor, entity.sensor) &&
                Arrays.equals(data, entity.data);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(sensor, time);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }

    @Override
    public String toString() {
        return "TestEntity{" +
                "sensor='" + sensor + '\'' +
                ", time=" + time +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
