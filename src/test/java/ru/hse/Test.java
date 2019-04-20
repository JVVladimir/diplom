package ru.hse;

import com.google.gson.Gson;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test {

    private static final Logger log = LoggerFactory.getLogger(Test.class);

    private static SerialPort serialPort;

    public static void main(String[] args) {
        //Передаём в конструктор имя порта
        serialPort = new SerialPort("/dev/ttyACM0");
        try {
            //Открываем порт
            serialPort.openPort();
            //Выставляем параметры
            serialPort.setParams(SerialPort.BAUDRATE_115200,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            //Включаем аппаратное управление потоком
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);
            // serialPort.set
            //Устанавливаем ивент лисенер и маску
            serialPort.addEventListener(new PortReader(), SerialPort.MASK_RXCHAR);
            serialPort.purgePort(SerialPort.PURGE_TXCLEAR | SerialPort.PURGE_RXCLEAR | SerialPort.PURGE_RXABORT | SerialPort.PURGE_TXABORT);
            //Отправляем запрос устройству
           // serialPort.writeString("Get data");
        }
        catch (SerialPortException ex) {
            System.out.println(ex);
        }

        try {
            Thread.currentThread().wait();
        } catch (Exception e) {
        }
    }

    private static class PortReader implements SerialPortEventListener {

        public void serialEvent(SerialPortEvent event) {
            if(event.getEventValue() > 0){
                try {
                    //Получаем ответ от устройства, обрабатываем данные и т.д.
                    byte[] data = serialPort.readBytes();
                    System.out.println(new String(data, "ASCII"));

                    Gson gson = new Gson();

                    TestEntity entity = gson.fromJson(new InputStreamReader(new ByteArrayInputStream(data)), TestEntity.class);
                    log.info("Data recieved: {}", entity);
                    //И снова отправляем запрос
                  //  serialPort.writeString("Get data");
                }
                catch (Exception ex) {
                    // System.out.println(ex);
                }
                System.out.println("------------------------------------------------------");
            }
        }
    }
}
