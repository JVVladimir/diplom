package ru.hse.GUI;

import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hse.GUI.controller.StartWindowController;
import ru.hse.arduino.ArduinoController;
import ru.hse.business.LeadSynchronizationManager;
import ru.hse.business.SynchronizationManager;
import ru.hse.net.NetManagerLead;

import java.util.Arrays;
import java.util.Map;


public class ClientGUI extends Application {

    private static final Logger log = LoggerFactory.getLogger(ClientGUI.class);

    private static final NetManagerLead netManagerLead = new NetManagerLead();
    private static final Map<String, String> mapClient = netManagerLead.runApp();

    private String comPort = "";
    private Map<String, String> map;

    public ClientGUI() {}


    public void startApp() { launch(null); }

    public void setComPort(String comPort) {
        this.comPort = comPort;
        netManagerLead.setSynchronizationManager(new LeadSynchronizationManager(this.comPort));

    }

    public String getComPort() { return comPort; }
    public Map<String, String> getMap() { return mapClient; }


    @Override
    public void start(Stage primaryStage) {
        String[] comPorts = ArduinoController.getConnectedComPorts();
        if (comPorts.length == 0) { log.info("Нет не одного подключенного устройства Arduino!");}
        else new StartWindowController(Arrays.asList(comPorts), this);
    }


    public static void main(String[] args) {
        new ClientGUI().startApp();

    }
}
