package ru.hse.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class UsersSearcher implements ConnectionListener {

    private static final Logger log = LoggerFactory.getLogger(UsersSearcher.class);

    private static final int SAME_PROGRAM = 1000;
    private static final int SAME_PROGRAM_SUBMIT = 1001;

    private ScannerIP scannerIP;
    private int port;
    private volatile Map<String, String> map;
    private ConcurrentLinkedQueue<Connection> listConnections;

    public UsersSearcher(int port) {
        this.port = port;
        this.scannerIP = new ScannerIP();
        this.map = new HashMap<>();
        this.listConnections = new ConcurrentLinkedQueue<>();
    }

    public Map<String, String> search() {
        List<String> ips = scannerIP.getNetworkIPs();
        ips.remove("192.168.1.65");
        log.info("Available ips: {}", ips);
        for (String ip : ips) {
            try {
                Connection connection = new Connection(this, ip, port);
                listConnections.add(connection);
                connection.sendMessage(new Message(SAME_PROGRAM));
            } catch (RuntimeException ignored) {
            }
        }
        waitConnections();
        return map;
    }

    private synchronized void waitConnections() {
        try {
            while(listConnections.size() != 0)
                wait();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onReceivedMessage(Connection connection, Object requestData) {
        if (requestData instanceof Message && ((Message) requestData).getCommand() == SAME_PROGRAM_SUBMIT) {
            map.put(connection.getRemoteIP(), ((Message) requestData).getName());
            listConnections.remove(connection);
        }
        checkWakeUp();
    }

    @Override
    public void onConnectionException(Connection connection, Throwable ex) {
        listConnections.remove(connection);
        log.info("IP address disabled: {}", connection.getRemoteIP());
        checkWakeUp();
    }

    private synchronized void checkWakeUp() {
        if(listConnections.size() == 0)
            notifyAll();
    }
}
