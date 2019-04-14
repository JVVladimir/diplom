package ru.hse;

import ru.hse.business.SynchronizationManager;
import ru.hse.learning_algorithm.LearningParadigm;
import ru.hse.tree_parity_machine.TreeParityMachine;

public class Main {

    public static void main(String[] args) {
        new Main();
    }

    Main() {
        String text = "Hello, Vova!";
        TreeParityMachine tpm1 = new TreeParityMachine(16, 16, -8, 8, LearningParadigm.HEBBIAN);
        SynchronizationManager manager = new SynchronizationManager(tpm1);
        manager.handleResponse(new byte[]{SynchronizationManager.INIT_W});
        new Thread(() -> {
            while (!manager.isSync()) {
                try {
                    Thread.currentThread().sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            byte[] ar = text.getBytes();
            byte[] res = new byte[ar.length + 1];
            System.arraycopy(ar, 0, res, 1, ar.length);
            res[0] = SynchronizationManager.ENCRYPT;
            manager.handleResponse(res);
        }).start();
    }

}
