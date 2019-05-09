package ru.hse;

import ru.hse.business.SynchronizationManager;
import ru.hse.business.entity.ResponseData;
import ru.hse.learning_algorithm.LearningParadigm;
import ru.hse.net.ScannerIP;
import ru.hse.tree_parity_machine.TreeParityMachine;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        new Main();
    }

    Main() {
        String message = "Hello, Vova!";
        TreeParityMachine tpm1 = new TreeParityMachine(8, 16, -2, 2, LearningParadigm.HEBBIAN);
        SynchronizationManager manager = new SynchronizationManager(tpm1);
        Scanner scanner = new Scanner(System.in);
        while(scanner.hasNext()) {
            String command = scanner.nextLine();
            if(command.equals("reg"))
                manager.generateKey();
            else if(command.equals("exit"))
                System.exit(0);
        }
        /*manager.generateKey();
        new Thread(() -> {
            while (!manager.isSync()) {
                try {
                    Thread.currentThread().sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            /*manager.handleResponse(new ResponseData(SynchronizationManager.ENCRYPT,
                    message.toCharArray(), (short)message.getBytes().length));
            manager.setCurCommand(SynchronizationManager.ENCRYPT);
        }).start();*/
    }
}
