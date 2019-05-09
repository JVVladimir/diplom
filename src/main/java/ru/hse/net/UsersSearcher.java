package ru.hse.net;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsersSearcher implements ConnectionListener {

    private static final byte SAME_PROGRAM = 120;
    private static final byte SAME_PROGRAM_SUBMIT = 121;

    private ScannerIP scannerIP;
    private int port;

    public UsersSearcher(int port) {
        this.port = port;
        this.scannerIP = new ScannerIP();
    }

    public Map<String, String> search() {
        Map<String, String> map = new HashMap<>();
        List<String> ips = scannerIP.getNetworkIPs();
        for (String ip : ips) {
            Connection connection = new Connection(this, ip, port);
            connection.sendMessage(new Message(SAME_PROGRAM));
        }
        return map;
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
