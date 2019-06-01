package ru.hse.GUI;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hse.GUI.controller.StartWindowController;
import ru.hse.arduino.ArduinoController;
import ru.hse.business.SlaveSynchronizationManager;
import ru.hse.net.NetManagerSlave;

import java.util.Arrays;

public class ClientGUISlave extends Application {


    private static final Logger log = LoggerFactory.getLogger(ClientGUILead.class);

    public static final NetManagerSlave netManagerSlave = new NetManagerSlave();
    public static final String username = netManagerSlave.runApp();

    private String comPort = "";

    public volatile boolean isMessage = false;
    public volatile String mess;

    public ClientGUISlave() {}


    public void startApp() { launch(null); }

    public void setComPort(String comPort) {
        this.comPort = comPort;
        netManagerSlave.setSynchronizationManager(new SlaveSynchronizationManager(this.comPort));
    }

    @Override
    public void start(Stage primaryStage) {
        String[] comPorts = ArduinoController.getConnectedComPorts();
        if (comPorts.length == 0) { log.info("Нет не одного подключенного устройства Arduino!");}
        else new StartWindowController(Arrays.asList(comPorts), this);
        new Thread(() -> {
            while (true) {
                while (!netManagerSlave.isSend) {
                    Thread.yield();
                }
                mess = netManagerSlave.mess;
                log.info("{}", mess);
                isMessage = true;
                netManagerSlave.isSend = false;
            }
        }).start();
    }


    public static void main(String[] args) {
        new ClientGUISlave().startApp();
    }
}
