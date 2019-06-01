package ru.hse.net;

public interface ConnectionListener {

    void onReceivedMessage(Connection connection, Object requestData);

    default void onConnectionReady(Connection connection) {}

    default void onConnectionClose(Connection connection) {}

    default void onConnectionException(Connection connection, Throwable ex) {}

}
