package ru.hse;

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
        arduinoController = new ArduinoController(null, "COM3", 57600, 8, 1, 0);
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

    @AfterAll
    public static void tearDown() {
        arduinoController.closePort();
    }

}
