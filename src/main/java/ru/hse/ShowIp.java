package ru.hse;

import java.net.InetAddress;
import java.util.Arrays;

class ShowIP {

    public static void main(String[] args) throws Exception {
        getNetworkIPs();
    }

    public static void getNetworkIPs() {
        final byte[] ip;
        try {
            ip = InetAddress.getLocalHost().getAddress();
        } catch (Exception e) {
            return;     // exit method, otherwise "ip might not have been initialized"
        }
        System.out.println(Arrays.toString(ip));
        for (int i = 1; i <= 254; i++) {
            final int j = i;  // i as non-final variable cannot be referenced from inner class
            // new thread for parallel execution
            new Thread(() -> {
                try {
                    ip[3] = (byte) j;
                    InetAddress address = InetAddress.getByAddress(ip);
                    String output = address.toString().substring(1);
                    if (address.isReachable(5000)) {
                        System.out.println(output + " is on the network");
                    } else {
                        System.out.println("Not Reachable: " + output);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();     // dont forget to start the thread
        }
    }
}
