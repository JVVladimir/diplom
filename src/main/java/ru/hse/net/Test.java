package ru.hse.net;

public class Test implements ConnectionListener{

    public static void main(String[] args) {
        new Test();
    }


    Test() {
        Connection connection = new Connection(this, );
    }

    @Override
    public void onReceivedMessage(Connection connection, Object requestData) {

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
