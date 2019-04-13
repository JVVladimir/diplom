package ru.hse.arduino;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hse.business.Handler;
import ru.hse.business.SynchronizationManager;

public class ArduinoController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(ArduinoController.class);
    private Handler handler;
    private SerialPort serialPort;

    public ArduinoController(SynchronizationManager manager, String comPortName, int baundRate, int dataBits, int stopBits, int parity) {
        this.handler = manager;
        SerialPort comPort = SerialPort.getCommPort(comPortName);
        log.info("Создан SerialPort с именем {}!", comPort);
        this.serialPort = comPort;
        serialPort.setComPortParameters(baundRate, dataBits, stopBits, parity);
    }

    public static SerialPort[] getAllComPorts() {
        return SerialPort.getCommPorts();
    }

    @Override
    public void openPort() {
        if (serialPort == null)
            throw new ControllerException("Попытка открыть несуществующий com порт");
        serialPort.addDataListener(this);
        serialPort.openPort();
        log.info("SerialPort: {} успешно открыт!", serialPort);
    }

    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_RECEIVED | SerialPort.LISTENING_EVENT_DATA_WRITTEN;
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_WRITTEN)
            log.info("All bytes were successfully transmitted!");
        else if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_RECEIVED) {
            byte[] newData = event.getReceivedData();
            log.info("Received data of size: {}", newData.length);
            handler.handleRequest(newData);
        }
    }

    @Override
    public void sendMessage(byte[] message) {
        if (!serialPort.isOpen())
            throw new ControllerException("Попытка записи в закрытый com порт");
        serialPort.writeBytes(message, message.length);
        log.info("Сообщение отправлено: {}!", message);
    }

    @Override
    public void closePort() {
        boolean flag = serialPort.closePort();
        if (flag)
            log.info("SerialPort: {} успешно закрыт!", serialPort);
        else
            log.info("SerialPort: {} не был открыт", serialPort);
    }

    public SerialPort getSerialPort() {
        return serialPort;
    }

    @Override
    public String toString() {
        return "ArduinoController{" +
                "handler=" + handler +
                ", serialPort=" + serialPort +
                '}';
    }
}
