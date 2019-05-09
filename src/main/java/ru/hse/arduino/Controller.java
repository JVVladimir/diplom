package ru.hse.arduino;

import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public interface Controller extends SerialPortEventListener {

    void openPort() throws SerialPortException;

    void sendMessage(byte[] message);

    void closePort() throws SerialPortException;

}
