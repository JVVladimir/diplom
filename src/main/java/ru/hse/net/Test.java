package ru.hse.net;

public class Test implements ConnectionListener {

    public static void main(String[] args) {
        new Test();
    }

    Test() {
        Server server = new Server(this, 15600);
    }

    @Override
    public void onReceivedMessage(Connection connection, Object requestData) {
        if (requestData instanceof Message) {
            Message message = (Message) requestData;
            System.out.println(requestData);
            if (message.getCommand() == 120) {
                connection.sendMessage(new Message(121, System.getProperty("user.name"), null));
            }
        }
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
