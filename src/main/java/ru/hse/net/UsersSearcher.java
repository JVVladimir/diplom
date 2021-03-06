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
        log.info("Available ips: {}", ips);
        for (String ip : ips) {
            try {
                Connection connection = new Connection(this, ip, port);
                listConnections.add(connection);
                connection.sendMessage(new Message(SAME_PROGRAM));
            } catch (RuntimeException ex) {
            }
        }
        while(listConnections.size() != 0)
            Thread.yield();
        return map;
    }

    @Override
    public void onReceivedMessage(Connection connection, Object requestData) {
        if (requestData instanceof Message && ((Message) requestData).getCommand() == SAME_PROGRAM_SUBMIT) {
            map.put(connection.getRemoteIP(), ((Message) requestData).getName());
            listConnections.remove(connection);
        }
    }


    @Override
    public void onConnectionException(Connection connection, Throwable ex) {
        listConnections.remove(connection);
        log.info("IP address disabled: {}",connection.getRemoteIP());
    }
}
