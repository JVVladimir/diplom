package ru.hse.net;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {

    private static final Logger log = LoggerFactory.getLogger(Server.class);

    private ConnectionListener connectionListener;
    private Connection connection;
    private ServerSocket serverSocket;

    public Server(ConnectionListener manager, int port) {
        this.connectionListener = manager;
        Thread t = new Thread(() -> {
            try {
                serverSocket = new ServerSocket(port);
                Socket socket = serverSocket.accept();
                connection = new Connection(connectionListener, socket);
                while (true) {
                    if (connection.isClosed()) {
                        socket = serverSocket.accept();
                        connection = new Connection(connectionListener, socket);
                    } else Thread.yield();
                }
            } catch (IOException e) {
                log.error("Ошибка создания нового соединения!");
                throw new RuntimeException(e);
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public void destroy() {
        if (serverSocket != null && !serverSocket.isClosed())
            close();
    }

    private void close() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

}