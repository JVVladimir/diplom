package ru.hse;

import com.fazecast.jSerialComm.SerialPort;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        SerialPort[] serialPorts = SerialPort.getCommPorts();
        System.out.println(Arrays.toString(serialPorts));
    }

}
