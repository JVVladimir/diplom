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
                    log.info("Выход с абонента 2: {}", message.getOut());
                    responseReceived = true;
                    break;
                case TRAIN:
                    synchronizationManager.setOut2(message.getOut());
                    log.info("Выход с абонента 2: {}", message.getOut());
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
        Scanner scanner = new Scanner(System.in);
        String message;
        RequestData data;
        while ((message = scanner.nextLine()) != null) {
            switch (message) {
                case "connect":
                    Map<String , String> map = new UsersSearcher(PORT).search();
                    if(map.size() == 0) {
                        log.info("Не найдено ни одного соединения!");
                        return;
                    }
                    Map.Entry<String, String> entry = map.entrySet().iterator().next();
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
                    log.info("Новый вход и выход получены (input) абонент 1: {}", data);
                    connection.sendMessage(new Message(INIT_X, data.getInput(), data.getOut()));
                    waitResponse();
                    break;
                case "train":
                    while (true) {
                        data = synchronizationManager.train();
                        epochs++;
                        log.info("Новый вход и выход получены (train) абонент 1: {}", data);
                        log.info("Выход абонента 1 и 2: {}, {}", data.getOut(), synchronizationManager.getOut2());
                        //TODO: simplify
                        if (data.getOut() == synchronizationManager.getOut2())
                            limit++;
                        else
                            limit = 0;
                        if(epochs == EPOCHS_MAX || limit == SYNC_LIMIT)
                            break;
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
