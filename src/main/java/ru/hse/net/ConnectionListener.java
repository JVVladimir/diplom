package ru.hse.net;

public interface ConnectionListener {

    void onReceivedMessage(Connection connection, Object requestData);

    void onConnectionReady(Connection connection);

    void onConnectionClose(Connection connection);

    void onConnectionException(Connection connection, Throwable ex);

}
