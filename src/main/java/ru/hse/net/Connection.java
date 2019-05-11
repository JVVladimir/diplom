package ru.hse.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

// TODO: возможно для большей безопасно стоит использовать SslSocket
public class Connection {

    private static final Logger log = LoggerFactory.getLogger(Connection.class);

    private Thread thread;
    private ConnectionListener listener;
    private Socket socket;
    private ObjectInputStream reader;
    private ObjectOutputStream writer;
    private String remoteIP;
    private OutputStream out;

    public Connection(ConnectionListener listener, String ip, int port) {
        this.remoteIP = ip;
        try {
            this.socket = new Socket(ip, port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        init(listener, socket);
    }

    public Connection(ConnectionListener listener, Socket socket) {
        this.socket = socket;
        init(listener, socket);
    }

    private void init(ConnectionListener listener, Socket socket) {
        this.listener = listener;
        try {
            out = socket.getOutputStream();
            writer = new ObjectOutputStream(new BufferedOutputStream(out));
            listen();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void listen() {
        thread = new Thread(() -> {
            try {
                listener.onConnectionReady(this);
                reader = new ObjectInputStream(socket.getInputStream());
                while (!Thread.interrupted()) {
                    Object ob = reader.readObject();
                    if (ob instanceof Message) {
                        Message message = (Message) ob;
                        int command = message.getCommand();
                        if (command == -10 || command == -5) {
                            close();
                            listener.onConnectionClose(this);
                        } else if (command == 1000) {
                            sendMessage(new Message(1001, System.getProperty("user.name"), null));
                            close();
                        } else {
                            listener.onReceivedMessage(this, ob);
                        }
                    } else {
                        listener.onReceivedMessage(this, ob);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                listener.onConnectionException(this, e);
                close();
                // throw new RuntimeException(e);
            }
        });
        thread.start();
    }

    public void sendMessage(Object responseData) {
        try {
            writer.writeObject(responseData);
            writer.flush();
        } catch (IOException ex) {
            listener.onConnectionException(this, ex);
            throw new RuntimeException(ex);
        }
    }

    public String getRemoteIP() {
        return remoteIP;
    }

    public boolean isClosed() {
        return socket.isClosed();
    }

    public void close() {
        thread.interrupt();
        try {
            writer.close();
            reader.close();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
