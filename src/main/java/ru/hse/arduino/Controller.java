package ru.hse.arduino;

import jssc.SerialPortEventListener;
import ru.hse.business.entity.ResponseData;

public interface Controller extends SerialPortEventListener {

    void openPort();

    void sendMessage(ResponseData message);

    void closePort();

}
