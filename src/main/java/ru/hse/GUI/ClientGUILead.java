package ru.hse.GUI;

import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hse.GUI.controller.StartWindowController;
import ru.hse.arduino.ArduinoController;
import ru.hse.business.LeadSynchronizationManager;
import ru.hse.business.entity.RequestData;
import ru.hse.net.Message;
import ru.hse.net.NetManagerLead;
import ru.hse.utils.Encrypter;

import java.util.Arrays;
import java.util.Map;


public class ClientGUILead extends Application {

    private static final Logger log = LoggerFactory.getLogger(ClientGUILead.class);

    public static final NetManagerLead netManagerLead = new NetManagerLead();
    private static final Map<String, String> mapClient = netManagerLead.runApp();

    private String comPort = "";

    public static boolean isReady =false;

    public ClientGUILead() {}


    public void startApp() { launch(null); }

    public void setComPort(String comPort) {
        this.comPort = comPort;
        netManagerLead.setSynchronizationManager(new LeadSynchronizationManager(this.comPort));
    }

    private void initWeights() {
        netManagerLead.synchronizationManager.initWeights();
        netManagerLead.connection.sendMessage(new Message(NetManagerLead.INIT_W));
        netManagerLead.waitResponse();
    }

    private void generateInput() {
        RequestData data = netManagerLead.synchronizationManager.initInput();
        netManagerLead.connection.sendMessage(new Message(NetManagerLead.INIT_X, data.getIn(), data.getOut()));
        netManagerLead.waitResponse();
    }

    private void train() {
        while (true) {
            RequestData data = netManagerLead.synchronizationManager.train();
            netManagerLead.epochs++;
            netManagerLead.limit = data.getOut() == netManagerLead.synchronizationManager.getOut2() ? ++netManagerLead.limit : 0;
            if(netManagerLead.epochs == NetManagerLead.EPOCHS_MAX || netManagerLead.limit == NetManagerLead.SYNC_LIMIT)
                break;
            netManagerLead.connection.sendMessage(new Message(NetManagerLead.TRAIN, data.getIn(), data.getOut()));
            netManagerLead.waitResponse();
        }
        log.info("epochs: {}", netManagerLead.epochs);
        netManagerLead.connection.sendMessage(new Message(NetManagerLead.SYNC_DONE));
        netManagerLead.waitResponse();
        netManagerLead.key = Encrypter.toBytes(netManagerLead.synchronizationManager.syncDone().getWeight());
        netManagerLead.isReady = true;
    }

    public void generateKey() {
        initWeights();
        generateInput();
        train();
        if (netManagerLead.isReady) isReady = true;
        else log.info("Абоненты еще не синхронизировались!");
    }


    public Map<String, String> getMap() { return mapClient; }

    @Override
    public void start(Stage primaryStage) {
        String[] comPorts = ArduinoController.getConnectedComPorts();
        if (comPorts.length == 0) { log.info("Нет не одного подключенного устройства Arduino!");}
        else new StartWindowController(Arrays.asList(comPorts), this);
    }


    public static void main(String[] args) {
        new ClientGUILead().startApp();
    }
}
