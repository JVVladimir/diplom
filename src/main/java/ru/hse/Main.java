package ru.hse;

import ru.hse.business.SynchronizationManager;
import ru.hse.business.entity.ResponseData;
import ru.hse.learning_algorithm.LearningParadigm;
import ru.hse.tree_parity_machine.TreeParityMachine;

public class Main {

    public static void main(String[] args) {
        new Main();
    }

    Main() {
        String text = "Hello, Vova!";
        TreeParityMachine tpm1 = new TreeParityMachine(8, 16, -2, 2, LearningParadigm.HEBBIAN);
        SynchronizationManager manager = new SynchronizationManager(tpm1);
        manager.handleResponse(new ResponseData(SynchronizationManager.INIT_W));
       /* new Thread(() -> {
            while (!manager.isSync()) {
                try {
                    Thread.currentThread().sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //short[] ar = text.getBytes();
            //manager.handleResponse(new ResponseData(SynchronizationManager.ENCRYPT).setVector(ar));
        }).start();*/
    }



}
