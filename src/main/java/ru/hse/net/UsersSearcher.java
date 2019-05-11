package ru.hse.net;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class UsersSearcher implements ConnectionListener {

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

    // TODO: метод очень медленный (7 сек), нужно ускорить
    public Map<String, String> search() {
        List<String> ips = scannerIP.getNetworkIPs();
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
}
