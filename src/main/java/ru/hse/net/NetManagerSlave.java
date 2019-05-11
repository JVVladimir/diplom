package ru.hse.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hse.business.LeadSynchronizationManager;
import ru.hse.business.SlaveSynchronizationManager;
import ru.hse.business.SynchronizationManager;
import ru.hse.business.entity.RequestData;
import ru.hse.utils.Encrypter;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;

public class NetManagerSlave implements ConnectionListener {

    private static final Logger log = LoggerFactory.getLogger(NetManagerSlave.class);


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

    public NetManagerSlave() {
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
            RequestData res;
            switch (message.getCommand()) {
                case CONNECT:
                    isReady = false;
                    connection.sendMessage(new Message(CONNECT));
                    synchronizationManager = new SlaveSynchronizationManager(8);
                    break;
                case INIT_W:
                    synchronizationManager.initWeights();
                    connection.sendMessage(new Message(INIT_W));
                    break;
                case INIT_X:
                    synchronizationManager.setInput(message.getInput());
                    synchronizationManager.setOut(message.getOut());
                    res = synchronizationManager.train();
                    connection.sendMessage(new Message(INIT_X, null, res.getOut()));
                    break;
                case TRAIN:
                    synchronizationManager.setInput(message.getInput());
                    synchronizationManager.setOut(message.getOut());
                    res = synchronizationManager.train();
                    connection.sendMessage(new Message(INIT_X, null, res.getOut()));
                    break;
                case SYNC_DONE:
                    res = synchronizationManager.syncDone();
                    connection.sendMessage(new Message(SYNC_DONE));
                    key = Encrypter.toBytes(res.getWeight());
                    isReady = true;
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
        while ((message = scanner.nextLine()) != null) {
            if (isReady)
                connection.sendMessage(new Message(SEND, System.getProperty("user.name"), Encrypter.encrypt(message.getBytes(), key)));
            else
                log.info("Абоненты еще не синхронизировались!");
            break;
        }
    }

    // TODO: подумать не переделать ли под поток, возвращающий результат задачи и таймер (вдруг задача не выполнится никогда)
    private void waitResponse() {
        while (!responseReceived)
            Thread.yield();
        responseReceived = false;
    }

    public static void main(String[] args) {
        new NetManagerSlave().runApp();
    }
}
