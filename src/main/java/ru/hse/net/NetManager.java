package ru.hse.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hse.business.LeadSynchronizationManager;
import ru.hse.business.SynchronizationManager;
import ru.hse.business.entity.RequestData;
import ru.hse.utils.Encrypter;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;

public class NetManager implements ConnectionListener {

    private static final Logger log = LoggerFactory.getLogger(NetManager.class);


    private static final int CONNECT = 200;
    private static final int INIT_W = 201;
    private static final int INIT_X = 202;
    private static final int TRAIN = 203;
    private static final int SYNC_DONE = 204;
    private static final int SEND = 205;
    private static final int RECEIVE = 206;
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

    public NetManager() {
        server = new Server(this, PORT);
    }

    @Override
    public void onConnectionReady(Connection connection) {

    }

    @Override
    public void onReceivedMessage(Connection connection, Object data) {
        log.info("Message received: {}", data);
        if (data instanceof Message) {
            Message message = (Message) data;
            switch (message.getCommand()) {
                case CONNECT:
                    responseReceived = true;
                    break;
                case INIT_W:
                    limit = 0;
                    epochs = 0;
                    responseReceived = true;
                    break;
                case INIT_X:
                    synchronizationManager.setOut(message.getOut());
                    responseReceived = true;
                    break;
                case TRAIN:
                    synchronizationManager.setOut(message.getOut());
                    responseReceived = true;
                    break;
                case SEND:
                    System.out.println(new String(Encrypter.decrypt(message.getMessage(), key), StandardCharsets.UTF_8));
                    break;
            }
        }
    }

    // TODO: networkCodes remove ????
    public void runApp() {
        Scanner scanner = new Scanner(System.in);
        String message;
        RequestData data;
        while ((message = scanner.nextLine()) != null) {
            switch (message) {
                case "connect":
                    Map.Entry<String, String> entry = new UsersSearcher(PORT).search().entrySet().iterator().next();
                    log.info("Found user: {}", entry);
                    // String name = entry.getValue();
                    String ip = entry.getKey();
                    try {
                        connection = new Connection(this, ip, PORT);
                        connection.sendMessage(new Message(CONNECT));
                        waitResponse();
                    } catch (RuntimeException ex) {
                        log.info("Не удалось подключиться к абоненту!");
                        return;
                    }
                    synchronizationManager = new LeadSynchronizationManager(8);
                    break;
                case "weights":
                    synchronizationManager.initWeights();
                    log.info("Веса сгенерированы!");
                    connection.sendMessage(new Message(INIT_W));
                    waitResponse();
                    break;
                case "input":
                    data = synchronizationManager.initInput();
                    log.info("Новый вход и выход получены (input): {}", data);
                    connection.sendMessage(new Message(INIT_X, data.getInput(), data.getOut()));
                    waitResponse();
                    break;
                case "train":
                    while (epochs < EPOCHS_MAX || limit < SYNC_LIMIT) {
                        data = synchronizationManager.train();
                        log.info("Новый вход и выход получены (train): {}", data);
                        if (data.getOut() == synchronizationManager.getOut2())
                            limit++;
                        else
                            limit = 0;
                        connection.sendMessage(new Message(TRAIN, data.getInput(), data.getOut()));
                        waitResponse();
                        log.info("Текущая эпоха: {}", epochs);
                    }
                    connection.sendMessage(new Message(SYNC_DONE));
                    waitResponse();
                    key = Encrypter.toBytes(synchronizationManager.syncDone().getWeight());
                    log.info("Сгенерированный ключ: {}", key);
                    isReady = true;
                    break;
                default:
                    if (isReady)
                        connection.sendMessage(new Message(SEND, System.getProperty("user.name"), Encrypter.encrypt(message.getBytes(), key)));
                    else
                        log.info("Абоненты еще не синхронизировались!");
                    break;
            }
        }
    }

    // TODO: подумать не переделать ли под поток, возвращающий результат задачи и таймер (вдруг задача не выполнится никогда)
    private void waitResponse() {
        while (!responseReceived)
            Thread.yield();
        responseReceived = false;
    }

    public static void main(String[] args) {
        new NetManager().runApp();
    }
}
