package ru.hse.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hse.GUI.ClientGUI;
import ru.hse.arduino.ArduinoController;
import ru.hse.business.LeadSynchronizationManager;
import ru.hse.business.SynchronizationManager;
import ru.hse.business.entity.RequestData;
import ru.hse.utils.Encrypter;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;

public class NetManagerLead implements ConnectionListener {

    private static final Logger log = LoggerFactory.getLogger(NetManagerLead.class);

    private static final int CONNECT = 200;
    private static final int INIT_W = 201;
    private static final int INIT_X = 202;
    private static final int TRAIN = 203;
    private static final int SYNC_DONE = 204;
    private static final int SEND = 205;
    private boolean isReady = false;
    private byte[] key;

    private int epochs, limit;
    private static final int SYNC_LIMIT = 40;
    private static final int EPOCHS_MAX = 150;

    private final static int PORT = 15600;

    private volatile boolean responseReceived;

    private Server server;
    private Connection connection;
    private SynchronizationManager synchronizationManager;

    public NetManagerLead() {
        server = new Server(this, PORT);
    }

    @Override
    public void onReceivedMessage(Connection connection, Object data) {
        if (data instanceof Message) {
            Message message = (Message) data;
            switch (message.getCommand()) {
                case CONNECT:
                    isReady = false;
                    responseReceived = true;
                    break;
                case INIT_W:
                    limit = 0;
                    epochs = 0;
                    responseReceived = true;
                    break;
                case INIT_X:
                    synchronizationManager.setOut2(message.getOut());
                    responseReceived = true;
                    break;
                case TRAIN:
                    synchronizationManager.setOut2(message.getOut());
                    responseReceived = true;
                    break;
                case SYNC_DONE:
                    responseReceived = true;
                    break;
                case SEND:
                    System.out.println(new String(Encrypter.decrypt(message.getMessage(), key), StandardCharsets.UTF_8));
                    break;
            }
        }
    }

    public void runApp() {
        ClientGUI clientGUI = new ClientGUI();
        Map<String , String> map = new UsersSearcher(PORT).search();
        if(map.size() == 0) {
            log.info("Не найдено ни одного соединения!");
            return;
        }
        Map.Entry<String, String> entry = map.entrySet().iterator().next();
        log.info("Found user: {}", entry);
        String ip = entry.getKey();
        try {
            connection = new Connection(this, ip, PORT);
            connection.sendMessage(new Message(CONNECT));
            waitResponse();
        } catch (RuntimeException ex) {
            log.info("Не удалось подключиться к абоненту!");
            return;
        }

        new Thread(clientGUI::startApp).start();

        while (clientGUI.getComPort().equals("")) { Thread.yield();}

        synchronizationManager = new LeadSynchronizationManager(clientGUI.getComPort());

        /*
        Scanner scanner = new Scanner(System.in);
        String message;
        RequestData data;
        while ((message = scanner.nextLine()) != null) {
            switch (message) {
                case "c":
                case "w":
                    synchronizationManager.initWeights();
                    connection.sendMessage(new Message(INIT_W));
                    waitResponse();
                    break;
                case "i":
                    data = synchronizationManager.initInput();
                    connection.sendMessage(new Message(INIT_X, data.getIn(), data.getOut()));
                    waitResponse();
                    break;
                case "t":
                    while (true) {
                        data = synchronizationManager.train();
                        epochs++;
                        limit = data.getOut() == synchronizationManager.getOut2() ? ++limit : 0;
                        if(epochs == EPOCHS_MAX || limit == SYNC_LIMIT)
                            break;
                        connection.sendMessage(new Message(TRAIN, data.getIn(), data.getOut()));
                        waitResponse();
                    }
                    log.info("epochs: {}", epochs);
                    connection.sendMessage(new Message(SYNC_DONE));
                    waitResponse();
                    key = Encrypter.toBytes(synchronizationManager.syncDone().getWeight());
                    isReady = true;
                    break;
                default:
                    if (isReady)
                        connection.sendMessage(new Message(SEND, System.getProperty("user.name"), Encrypter.encrypt(message.getBytes(), key)));
                    else
                        log.info("Абоненты еще не синхронизировались!");
                    break;
            }
        }*/
    }

    // TODO: подумать не переделать ли под поток, возвращающий результат задачи и таймер (вдруг задача не выполнится никогда)
    private void waitResponse() {
        while (!responseReceived)
            Thread.yield();
        responseReceived = false;
    }

    public static void main(String[] args) {
        new NetManagerLead().runApp();
    }
}
