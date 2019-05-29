package ru.hse.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hse.business.SlaveSynchronizationManager;
import ru.hse.business.SynchronizationManager;
import ru.hse.business.entity.RequestData;
import ru.hse.utils.Encrypter;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;

public class NetManagerSlave implements ConnectionListener {

    private static final Logger log = LoggerFactory.getLogger(NetManagerSlave.class);


    public static final int CONNECT = 200;
    public static final int INIT_W = 201;
    public static final int INIT_X = 202;
    public static final int TRAIN = 203;
    public static final int SYNC_DONE = 204;
    public static final int SEND = 205;
    public boolean isReady = false;
    private boolean isConnect = false;
    private String userName = "";
    public byte[] key;

    private final static int PORT = 15600;

    private volatile boolean responseReceived;

    private Server server;
    public Connection connection;
    public SynchronizationManager synchronizationManager;
    public volatile boolean isSend;
    public String mess;

    public NetManagerSlave() {
        server = new Server(this, PORT);
    }

    @Override
    public void onConnectionReady(Connection connection) { }

    @Override
    public void onReceivedMessage(Connection connection, Object data) {
        log.info("Message received: {}", data);
        if (data instanceof Message) {
            Message message = (Message) data;
            RequestData res;
            switch (message.getCommand()) {
                case CONNECT:
                    isReady = false;
                    isConnect = true;
                    userName = message.getName();
                    connection.sendMessage(new Message(CONNECT));
                    this.connection = connection;
                    break;
                case INIT_W:
                    synchronizationManager.initWeights();
                    connection.sendMessage(new Message(INIT_W));
                    break;
                case INIT_X:
                    synchronizationManager.setInput(message.getInput());
                    synchronizationManager.setOut2(message.getOut());
                    log.info("Вход и выход от абонента 1 {}", message);
                    res = synchronizationManager.train();
                    connection.sendMessage(new Message(INIT_X, null, res.getOut()));
                    break;
                case TRAIN:
                    synchronizationManager.setInput(message.getInput());
                    synchronizationManager.setOut2(message.getOut());
                    log.info("Вход и выход от абонента 1 {}", message);
                    res = synchronizationManager.train();
                    connection.sendMessage(new Message(TRAIN, null, res.getOut()));
                    break;
                case SYNC_DONE:
                    res = synchronizationManager.syncDone();
                    connection.sendMessage(new Message(SYNC_DONE));
                    key = Encrypter.toBytes(res.getWeight());
                    log.info("Generated key: {}", key);
                    isReady = true;
                    break;
                case SEND:
                    isSend = true;
                    mess = new String(message.getMessage(), StandardCharsets.UTF_8);
                   // System.out.println(new String(Encrypter.decrypt(message.getMessage(), key), StandardCharsets.UTF_8));
                    break;
            }
        }
    }

    public String runApp() {
        while(!isConnect) {
            Thread.yield();
        }
        return userName;
    }

    public void setSynchronizationManager(SynchronizationManager synchronizationManager) {
        this.synchronizationManager = synchronizationManager;
    }

    // TODO: подумать не переделать ли под поток, возвращающий результат задачи и таймер (вдруг задача не выполнится никогда)
    private void waitResponse() {
        while (!responseReceived)
            Thread.yield();
        responseReceived = false;
    }

}
