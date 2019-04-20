package ru.hse.arduino;


import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortException;
import jssc.SerialPortList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hse.business.Handler;
import ru.hse.business.SynchronizationManager;

import java.io.UnsupportedEncodingException;

public class ArduinoController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(ArduinoController.class);
    private Handler handler;
    private SerialPort serialPort;

    private String data;
    private String newStrData;

    public ArduinoController(SynchronizationManager manager, String comPortName, int baundRate) {
        this.handler = manager;
        SerialPort comPort = new SerialPort(comPortName);
        log.info("Создан SerialPort с именем {}!", comPort);
        this.serialPort = comPort;
        try {
            serialPort.setParams(baundRate,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE, false, false);
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);
        } catch (SerialPortException e) {
            throw new ControllerException("Ошибка в настройке параметров контроллера!");
        }
    }

    public static String[] getAllComPorts() {
        return SerialPortList.getPortNames();
    }

    @Override
    public void openPort() {
        if (serialPort == null)
            throw new ControllerException("Попытка открыть несуществующий com порт");
        try {
            serialPort.openPort();
            if (!serialPort.isOpened())
                throw new ControllerException("Невозможно открыть com порт");
            serialPort.addEventListener(this, SerialPort.MASK_RXCHAR);
            log.info("SerialPort: {} успешно открыт!", serialPort);
        } catch (SerialPortException e) {
            throw new ControllerException("Ошибка открытия порта или добавления слушателя!");
        }
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        if (event.getEventValue() > 0) {
            String newData;
            try {
                newData = serialPort.readString();
            } catch (SerialPortException e) {
                throw new ControllerException("Ошибка в чтении данных!");
            }
            log.info("Received data of size: {}, data: {}", newData.length(), newData);
            handler.handleRequest(newData);
            data = newData;
        }
    }

    @Override
    public void sendMessage(String message) {
        if (!serialPort.isOpened())
            throw new ControllerException("Попытка записи в закрытый com порт");
        try {
            serialPort.writeString(message, "ASCII");
        } catch (SerialPortException e) {
            throw new ControllerException("Ошибка при отправке данных!");
        } catch (UnsupportedEncodingException ignored) {
        }
        log.info("Сообщение отправлено: {}!", message);
    }

    @Override
    public void closePort() {
        boolean flag;
        try {
            flag = serialPort.closePort();
        } catch (SerialPortException e) {
            throw new ControllerException("Ошибка закрытии порта!");
        }
        if (flag)
            log.info("SerialPort: {} успешно закрыт!", serialPort);
        else
            log.info("SerialPort: {} не был открыт", serialPort);
    }

    public SerialPort getSerialPort() {
        return serialPort;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ArduinoController{" +
                "handler=" + handler +
                ", serialPort=" + serialPort +
                '}';
    }

  /*  public byte[] fromJsonToByte(String json) {
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

    }*/
}
