package ru.hse.arduino;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hse.business.Handler;
import ru.hse.business.SynchronizationManager;

import java.io.BufferedOutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ArduinoController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(ArduinoController.class);
    private Handler handler;
    private SerialPort serialPort;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    private byte[] data;
    private String newStrData;

    public ArduinoController(SynchronizationManager manager, String comPortName, int baundRate, int dataBits, int stopBits, int parity) {
        this.handler = manager;
        SerialPort comPort = SerialPort.getCommPort(comPortName);
        log.info("Создан SerialPort с именем {}!", comPort);
        this.serialPort = comPort;
        serialPort.setComPortParameters(baundRate, dataBits, stopBits, parity);
        serialPort.clearRTS();
        serialPort.clearDTR();
        serialPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
    }

    public static SerialPort[] getAllComPorts() {
        return SerialPort.getCommPorts();
    }

    @Override
    public void openPort() {
        if (serialPort == null)
            throw new ControllerException("Попытка открыть несуществующий com порт");
        serialPort.openPort();
        if (!serialPort.isOpen())
            throw new ControllerException("Невозможно открыть com порт");
        serialPort.addDataListener(this);
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
            try {
                newStrData =new String(newData, "ASCII");
                log.info("JSON: {}", newStrData);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            byte[] newDataJson = fromJsonToByte(newStrData);
            log.info("Received data of size: {}, data: {}", newDataJson.length, newDataJson);
            handler.handleRequest(newDataJson);
            data = newDataJson;

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

    public byte[] fromJsonToByte(String json) {
        List<Byte> b = new ArrayList<>();
        StringBuilder newNumber = new StringBuilder();
        for(int i = 0; i<json.length(); i++){
            char c = json.charAt(i);
            if (c== ',' || c==']'|| c=='[') {
                if (!newNumber.toString().equals("")) {
                    b.add((byte) Integer.parseInt(newNumber.toString()));
                    newNumber = new StringBuilder();
                }
            }
            else newNumber.append(c);

        }
        byte[] br = new byte[b.size()];
        for (int j = 0; j<br.length; j++)
            br[j] = b.get(j);
        return br;

    }
}
