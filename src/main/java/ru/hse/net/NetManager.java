package ru.hse.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hse.business.SynchronizationManager;

import java.util.Scanner;

public class NetManager implements ConnectionListener {

    private static final Logger log = LoggerFactory.getLogger(NetManager.class);

    private static final int CONNECT = 200;
    private static final int INIT_W = 201;
    private static final int INIT_X = 202;
    private static final int LEARNING = 203;
    private static final int SYNC_DONE = 204;
    private static final int SEND = 205;
    private static final int RECEIVE = 206;
    private boolean isReady = false;

    private final static int PORT = 15600;

    private Server server;
    private SynchronizationManager synchronizationManager;

    public NetManager() {
        new Server(this, PORT);
    }

    @Override
    public void onReceivedMessage(Connection connection, Object requestData) {
        log.info("Message received: {}", requestData);
    }

    @Override
    public void onConnectionReady(Connection connection) {

    }

    @Override
    public void onConnectionClose(Connection connection) {

    }

    @Override
    public void onConnectionException(Connection connection, Throwable ex) {

    }

    public void runApp() {
        Scanner scanner = new Scanner(System.in);
        String message;
        while ((message = scanner.nextLine()) != null) {
            switch (message) {
                case "connect":
                    //установить соединение
                    break;
                case "init weights":
                    //генерировать вес
                    break;
                case "init X":
                    //генерировать Х
                    break;
                default:
                    if (isReady) {
                        //отправить сообщение
                    } else System.out.println("Абоненты еще не синхронизировались");
                    break;
            }
        }
    }
}


