package ru.hse.arduino;


import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortException;
import jssc.SerialPortList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hse.business.SynchronizationManager;
import ru.hse.business.entity.RequestData;
import ru.hse.business.entity.ResponseData;

public class ArduinoController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(ArduinoController.class);
    private SynchronizationManager handler;
    private SerialPort serialPort;

    private Gson gson;

    public ArduinoController(SynchronizationManager manager, String comPortName, int baundRate) {
        this.handler = manager;
        this.gson = new Gson();
        this.serialPort = new SerialPort(comPortName);
        log.info("Создан SerialPort с именем {}!", serialPort);
        openPort();
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

    public static String[] getConnectedComPorts() {
        return SerialPortList.getPortNames();
    }

    @Override
    public void openPort() {
        if (serialPort == null)
            throw new ControllerException("Попытка открыть несуществующий com порт");
        if (serialPort.isOpened()) return;
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

    private static final int LIMIT = 12;
    private StringBuilder str = new StringBuilder();
    private double count;
    private RequestData newEntity;
    private static final int DELAY = 30;

    @Override
    public void serialEvent(SerialPortEvent event) {
        if (event.getEventValue() > 0) {
            String newData;
            try {
                newData = serialPort.readString();
            } catch (SerialPortException e) {
                throw new ControllerException("Ошибка в чтении данных!");
            }
            log.info("Received requestData of size: {}, requestData: {}", newData.length(), newData);
            try {
                count++;
                str.append(newData);
                try {
                    newEntity = gson.fromJson(str.toString(), RequestData.class);
                    log.info("RequestData recieved: {}", newEntity);
                    str = new StringBuilder();
                    count = 0;
                } catch (JsonSyntaxException ignored) {
                    if (count == LIMIT) {
                        str = new StringBuilder();
                        count = 0;
                    }
                    return;
                }
            } catch (Exception ignored) {
            }
            if (newEntity != null) {
                sleep();
                handler.handleRequest(newEntity);
            } else handler.handleResponse(new ResponseData(handler.getCurCommand()));
        }
    }

    private void sleep() {
        try {
            Thread.sleep(ArduinoController.DELAY);
        } catch (InterruptedException e) {
        }
    }

    @Override
    public void sendMessage(ResponseData message) {
        if (!serialPort.isOpened())
            throw new ControllerException("Попытка записи в закрытый com порт");
        try {
            serialPort.writeString(gson.toJson(message));
        } catch (SerialPortException e) {
            throw new ControllerException("Ошибка при отправке данных!");
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

    @Override
    public String toString() {
        return "ArduinoController{" +
                "handler=" + handler +
                ", serialPort=" + serialPort +
                '}';
    }
}
