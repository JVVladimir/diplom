package ru.hse.net;

import java.io.Serializable;
import java.util.Arrays;

public class Message implements Serializable {

    private final int command;
    private final String name;
    private final byte[] message;
    private final short[] input;
    private final short out;

    // TODO: подумать как тут лучше сделать конструкторы без этого говна внутри них
    public Message(int command, String name, byte[] message) {
        this.command = command;
        this.name = name;
        this.message = message;
        this.input = null;
        this.out = 0;
    }

    public Message(int command, short[] input, short out) {
        this.command = command;
        this.input = input;
        this.out = out;
        this.name = null;
        this.message = null;
    }

    public Message(int command) {
        this.command = command;
        this.name = null;
        this.message = null;
        this.input = null;
        this.out = -1;
    }

    public int getCommand() {
        return command;
    }

    public String getName() {
        return name;
    }

    public byte[] getMessage() {
        return message;
    }

    public short[] getInput() {
        return input;
    }

    public short getOut() {
        return out;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Arrays.equals(this.message, message.message);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(message);
    }

    @Override
    public String toString() {
        return "Message{" +
                "command=" + command +
                ", name='" + name + '\'' +
                ", message=" + Arrays.toString(message) +
                ", input=" + Arrays.toString(input) +
                ", out=" + out +
                '}';
    }
}
