package ru.hse.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetManager implements ConnectionListener {

    private static final Logger log = LoggerFactory.getLogger(NetManager.class);

    private final static int PORT = 15600;

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


}
