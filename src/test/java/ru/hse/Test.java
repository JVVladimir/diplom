package ru.hse;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;

public class Test {

    private static final Logger log = LoggerFactory.getLogger(Test.class);

    private SerialPort serialPort;

    public static void main(String[] args) {
        new Test();
    }

    Test() {
        serialPort = new SerialPort("/dev/ttyACM0");
        try {
            serialPort.openPort();
            serialPort.setParams(SerialPort.BAUDRATE_115200,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE, false, false);
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);

            PortReader reader = new PortReader();
            serialPort.addEventListener(reader, SerialPort.MASK_RXCHAR);
            reader.send();
        } catch (SerialPortException ex) {
        }
        try {
            Thread.currentThread().wait();
        } catch (Exception e) {
        }
    }

    // цель 550 - 800к итераций

    public class PortReader implements SerialPortEventListener {

        private static final int LIMIT = 8;
        StringBuilder str = new StringBuilder();
        Gson gson = new Gson();
        double count = 0, countAll = 0, countMiss = 0;
        LocalDateTime time;
        boolean flag = false, flag2 = false;
        TestEntity prevEntity;

        public void serialEvent(SerialPortEvent event) {
            if (!flag) {
                send();
                flag = true;
                return;
            }
            if(!flag2) {
                time = LocalDateTime.now();
                flag2 = true;
            }
            if (event.getEventValue() > 0 && !LocalDateTime.now().isAfter(time.plusSeconds(30))) {
                String data;
                countAll++;
                try {
                    count++;
                    data = serialPort.readString();
                    // System.out.println(data);
                    str.append(data);
                    TestEntity entity;
                    try {
                        entity = gson.fromJson(str.toString(), TestEntity.class);
                        log.info("ResponseData recieved: {}", entity);
                        str = new StringBuilder();
                        count = 0;
                        if (!prevEntity.equals(entity))
                            System.out.println("Object not equal");
                    } catch (JsonSyntaxException ex) {
                    }
                    if (count == LIMIT) {
                        countMiss++;
                        str = new StringBuilder();
                        count = 0;
                    }
                } catch (Exception ex) {
                }
            } else {
                System.out.println(String.format("Промахов: %.2f%%, всего итераций: %.0f, 130 итераций за: %.2f сек.",
                        ((countMiss / countAll) * 100), countAll, 130.0 / (countAll / 30)));
                System.exit(0);
            }
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
            }
            send();
        }

        public void send() {
            TestEntity entity2 = new TestEntity();

            entity2.sensor = "Vova";
            entity2.time = 12324423;
            entity2.data = new java.util.Random().ints(30, -1, 1 + 1).toArray();

            try {
                serialPort.writeString(gson.toJson(entity2), "ASCII");
                log.info("ResponseData sent: {}", entity2);
            } catch (SerialPortException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            prevEntity = entity2;
        }
    }
}
