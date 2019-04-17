package ru.hse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hse.arduino.ArduinoController;
import ru.hse.arduino.ControllerException;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ControllerArduinoTest {

    private static final Logger log = LoggerFactory.getLogger(ControllerArduinoTest.class);
    private static ArduinoController arduinoController;
    private LocalTime time = LocalTime.now();

    @BeforeAll
    public static void setup() {
        arduinoController = new ArduinoController(null, "COM3", 9600, 8, 1, 0);
        arduinoController.openPort();
    }

    @Test
    public void testGetAllPorts() {
        log.info(Arrays.stream(ArduinoController.getAllComPorts()).collect(Collectors.toList()).toString());
    }

    @Test
    public void testSendMessage() {
        String text = "Hello";
        arduinoController.sendMessage(text.getBytes());
    }

    @Test
    public void testReceiveMessage() {
       Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
            while (LocalTime.now().isBefore(time.plusSeconds(10)))
                if (arduinoController.getData() != null)
                    return;
            throw new ControllerException("Данные от контроллера не получены!");
        });
    }

    @Test
    public void myTest() {
        String json = "{\n" +
                "\"value\": 42,\n" +
                "\"lat\": 48.748010,\n" +
                "\"lon\": 2.293491\n" +
                "}\n";
        /*GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();*/

        arduinoController.sendMessage(json.getBytes());
    }

    @AfterAll
    public static void tearDown() {
     //   arduinoController.closePort();
    }


    private short[] key = new short[]{ 0, -2, 0, -1, 2, 2, -1, 0, 0, -2, 0, -1, 2, 2, -1, 0, 0, -2, 0, -1, 2, 2, -1, 0, 0, -2, 0, -1, 2, 2, -1, 0, 0, -2, 0, -1, 2, 2, -1, 0, 0, -2, 0, -1, 2, 2, -1, 0, 0, -2, 0, -1, 2, 2, -1, 0, 0, -2, 0, -1, 2, 2, -1, 0, 0, -2, 0, -1, 2, 2, -1, 0, 0, -2, 0, -1, 2, 2, -1, 0, 0, -2, 0, -1, 2, 2, -1, 0, 0, -2, 0, -1, 2, 2, -1, 0, 0, -2, 0, -1, 2, 2, -1, 0, 0, -2, 0, -1, 2, 2, -1, 0, 0, -2, 0, -1, 2, 2, -1, 0, 0, -2, 0, -1, 2, 2, -1, 0};
    private short[] text = new short[]{72, 101, 108, 108, 111, 44, 32, 86, 111, 118, 97, 33};
    // -22, -41, 78, -98, -99, -34, 66, -92, 93, -108, 83, -45, 13, 10
    /*@Test
    public void testKey() {
        char[] newKey = weightToKey(key,(short)32);
        char[] messageShifr = shifr(text, (short) text.length, newKey, (short) newKey.length);
        for (int i =0; i<messageShifr.length; i++) {
            System.out.println((int)messageShifr[i]);
        }
    }


    //[2, 2, 0, 1, -1, 2, 1, 2, 2, 2, 0, 1, -1, 2, 1, 2, 2, 2, 0, 1, -1, 2, 1, 2, 2, 2, 0, 1, -1, 2, 1, 2, 2, 2, 0, 1, -1, 2, 1, 2, 2, 2, 0, 1, -1, 2, 1, 2, 2, 2, 0, 1, -1, 2, 1, 2, 2, 2, 0, 1, -1, 2, 1, 2, 2, 2, 0, 1, -1, 2, 1, 2, 2, 2, 0, 1, -1, 2, 1, 2, 2, 2, 0, 1, -1, 2, 1, 2, 2, 2, 0, 1, -1, 2, 1, 2, 2, 2, 0, 1, -1, 2, 1, 2, 2, 2, 0, 1, -1, 2, 1, 2, 2, 2, 0, 1, -1, 2, 1, 2, 2, 2, 0, 1, -1, 2, 1, 2]
    char[] shifr(char input[], short lenInput, char key[], short lenKey) {
        char[] res = new char[lenInput];
        for (int i = 0; i < lenInput; i++) {
            res[i] =(char) (input[i] ^ key[i % lenKey]);
        }
        return res;
    }

    char[] weightToKey(short key[], short lenKey) {
        char[] mas = new char[lenKey];
        for (int i = 0, j = 0; i < lenKey; i++) {
            mas[i] |= key[j];
            j++;
            mas[i] = (char)(mas[i] << 4);
            mas[i] |= key[j];
            j++;
        }
        return mas;
    }*/
}
