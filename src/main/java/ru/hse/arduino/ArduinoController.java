package ru.hse.arduino;


import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortException;
import jssc.SerialPortList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hse.business.Handler;
import ru.hse.business.SynchronizationManager;
import ru.hse.business.entity.RequestData;
import ru.hse.business.entity.ResponseData;

import java.io.UnsupportedEncodingException;

public class ArduinoController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(ArduinoController.class);
    private Handler handler;
    private SerialPort serialPort;

    private RequestData requestData;
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

    public static String[] getAllComPorts() {
        return SerialPortList.getPortNames();
    }

    private void openPort() {
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

    private static final int LIMIT = 8;
    private StringBuilder str = new StringBuilder();
    private double count = 0, countAll = 0, countMiss = 0;
    private RequestData newEntity = null;
    private static final int DELAY = 30;

    private boolean flag = false;

    @Override
    public void serialEvent(SerialPortEvent event) {
        if (!flag) {
            flag = true;
            return;
        }
        if (event.getEventValue() > 0) {
            String newData;
            try {
                newData = serialPort.readString();
            } catch (SerialPortException e) {
                throw new ControllerException("Ошибка в чтении данных!");
            }
            log.info("Received requestData of size: {}, requestData: {}", newData.length(), newData);
            countAll++;
            try {
                count++;
                System.out.println(newData);
                str.append(newData);
                try {
                    newEntity = gson.fromJson(newData, RequestData.class);
                    log.info("RequestData recieved: {}", newEntity);
                    str = new StringBuilder();
                    count = 0;
                } catch (JsonSyntaxException ignored) {
                }
                if (count == LIMIT) {
                    countMiss++;
                    str = new StringBuilder();
                    count = 0;
                }
            } catch (Exception ignored) {
            }
            if (newEntity != null) {
                sleep();
                handler.handleRequest(newEntity);
                requestData = newEntity;
                newEntity = null;
            }
        }
    }

    public void printStat() {
        System.out.println(String.format("Промахов: %.2f%%, всего итераций: %.0f, 130 итераций за: %.2f сек.",
                ((countMiss / countAll) * 100), countAll, 130.0 / (countAll / 30)));
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
            serialPort.writeString(gson.toJson(message), "ASCII");
        } catch (SerialPortException e) {
            throw new ControllerException("Ошибка при отправке данных!");
        } catch (UnsupportedEncodingException ignored) {
        }
        log.info("Сообщение отправлено: {}!", message);
    }

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

    public RequestData getRequestData() {
        return requestData;
    }

    public void setRequestData(RequestData requestData) {
        this.requestData = requestData;
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
