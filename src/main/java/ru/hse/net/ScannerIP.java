package ru.hse.net;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class ScannerIP {

    private static final int TIMEOUT = 1500;
    private static final int THREAD_COUNT = 150;
    private static final int NUM_ADDRESSES = 254;

    public List<String> getNetworkIPs() {
        byte[] ip;
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("1.1.1.1"), 7777);
            ip = socket.getLocalAddress().getAddress();
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
        ExecutorService service = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Callable<String>> todo = new ArrayList<>(NUM_ADDRESSES);
        for (int i = 2; i <= NUM_ADDRESSES; i++) {
            ip[3] = (byte) i;
            todo.add(new Scanner(Arrays.copyOf(ip, ip.length)));
        }
        List<Future<String>> futures;
        try {
            futures = service.invokeAll(todo);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        service.shutdown();
        return futures.stream().map(e -> {
            try {
                return e.get();
            } catch (InterruptedException | ExecutionException ignored) {
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private class Scanner implements Callable<String> {

        private byte[] ip;

        Scanner(byte[] ip) {
            this.ip = ip;
        }

        @Override
        public String call() {
            try {
                InetAddress address = InetAddress.getByAddress(ip);
                if (address.isReachable(TIMEOUT))
                    return address.toString().substring(1);
            } catch (IOException ignored) {
            }
            return null;
        }
    }
}
