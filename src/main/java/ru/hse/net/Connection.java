package ru.hse.net;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hse.business.entity.RequestData;
import ru.hse.business.entity.ResponseData;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class Connection {

    private static final Logger log = LoggerFactory.getLogger(Connection.class);

    private Thread thread;
    private ConnectionListener listener;
    private Socket socket;
    private ObjectInputStream reader;
    private ObjectOutputStream writer;

    public Connection(ConnectionListener listener, String ip, int port) {
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
            writer = new ObjectOutputStream(socket.getOutputStream());
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
                    if(ob instanceof Message ){
                        Message message = (Message) ob;
                        int command = message.getCommand();
                        if (command == -5) {
                            sendMessage(new Message(-10));
                            close();
                        } else if (command == 1000) {
                            sendMessage(new Message(1001, System.getProperty("user.name"), null));
                            sendMessage(new Message(-10));
                            close();
                        }
                    }else {
                        listener.onReceivedMessage(this, ob);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
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

    public void close() {
        thread.interrupt();
        try {
            writer.close();
            reader.close();
            socket.close();
        }catch (IOException ex) {}
        listener.onConnectionClose(this);
    }

}
