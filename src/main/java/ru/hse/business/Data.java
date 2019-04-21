package ru.hse.business;

import java.util.Arrays;
import java.util.Objects;

public class Data {

    private int command;
    private int[] vector[];
    private int out;
    private int memory;

    public int getCommand() {
        return command;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    public int[][] getVector() {
        return vector;
    }

    public void setVector(int[][] vector) {
        this.vector = vector;
    }

    public int getOut() {
        return out;
    }

    public void setOut(int out) {
        this.out = out;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Data data = (Data) o;
        return command == data.command &&
                out == data.out &&
                memory == data.memory &&
                Arrays.equals(vector, data.vector);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(command, out, memory);
        result = 31 * result + Arrays.hashCode(vector);
        return result;
    }

    @Override
    public String toString() {
        return "Data{" +
                "command=" + command +
                ", vector=" + Arrays.toString(vector) +
                ", out=" + out +
                ", memory=" + memory +
                '}';
    }
}
