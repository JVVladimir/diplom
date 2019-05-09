package ru.hse.net;

import java.io.Serializable;
import java.util.Arrays;

public class Message implements Serializable {

    private final int command;
    private final String name;
    private final byte[] data;

    public Message(int command, String name, byte[] data) {
        this.command = command;
        this.name = name;
        this.data = data;
    }

    public Message(int command) {
        this.command = command;
        this.name = null;
        this.data = null;
    }

    public int getCommand() {
        return command;
    }

    public String getName() {
        return name;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Arrays.equals(data, message.data);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

    @Override
    public String toString() {
        return "Message{" +
                "command=" + command +
                ", name='" + name + '\'' +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
