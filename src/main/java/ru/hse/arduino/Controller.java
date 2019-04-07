package ru.hse.arduino;

import com.fazecast.jSerialComm.SerialPortDataListener;

public interface Controller extends SerialPortDataListener {

    void openPort();

    void sendMessage(byte[] message);

    void closePort();

}
