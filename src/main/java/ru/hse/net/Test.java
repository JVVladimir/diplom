package ru.hse.net;

public class Test implements ConnectionListener {

    public static void main(String[] args) {
        new Test();
    }

// 192.168.1.64
    Test() {
        System.out.println("Hello");
        Connection connection = new Connection(this, "192.168.1.64",15600);
        connection.sendMessage("Vova krasava!");
    }

    @Override
    public void onReceivedMessage(Connection connection, Object requestData) {
        System.out.println(requestData);
    }

    @Override
    public void onConnectionReady(Connection connection) {
        System.out.println("I am ready");
    }

    @Override
    public void onConnectionClose(Connection connection) {

    }

    @Override
    public void onConnectionException(Connection connection, Throwable ex) {

    }
}
