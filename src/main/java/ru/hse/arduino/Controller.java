package ru.hse.arduino;

import jssc.SerialPortEventListener;

public interface Controller extends SerialPortEventListener {

    void openPort();

    void sendMessage(String message);

    void closePort();

}
