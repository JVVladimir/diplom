package ru.hse.GUI;

import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hse.GUI.controller.StartWindowController;
import ru.hse.arduino.ArduinoController;
import java.util.Arrays;



public class ClientGUI extends Application {

    private static final Logger log = LoggerFactory.getLogger(ClientGUI.class);

    private String comPort = "";

    public void startApp() { launch(null); }

    public void setComPort(String comPort) { this.comPort = comPort; }

    public String getComPort() { return this.comPort; }

    @Override
    public void start(Stage primaryStage) {
        String[] comPorts = ArduinoController.getConnectedComPorts();
        if (comPorts.length == 0) { log.info("Нет не одного подключенного устройства Arduino!");}
        else new StartWindowController(Arrays.asList(comPorts), this);
    }

}
