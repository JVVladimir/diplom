package ru.hse.net;

import java.util.Scanner;

public class Test2 implements ConnectionListener {

    public static void main(String[] args) {
        new Test2();
    }

// 192.168.1.64
    Test2() {
        System.out.println(new UsersSearcher(15600).search());
        /*Scanner scanner = new Scanner(System.in);
        Connection connection = new Connection(this, "192.168.1.64",15600);
        connection.sendMessage(new Message(120));
        String s;
        while((s = scanner.nextLine()) != null) {
            if (s.equals("exit")) {
                connection.sendMessage(new Message(-5));
            } else {
                connection.sendMessage(new Message(100, s, null));
            }
        }*/
    }

    @Override
    public void onReceivedMessage(Connection connection, Object requestData) {
        if(requestData instanceof Message) {
            Message message = (Message) requestData;
            System.out.println(message + "   " + message.getName());
        }
    }

    @Override
    public void onConnectionReady(Connection connection) {
        System.out.println("I am ready");
    }

    @Override
    public void onConnectionClose(Connection connection) {
        System.out.println("Connection closed!");
    }

    @Override
    public void onConnectionException(Connection connection, Throwable ex) {

    }
}
