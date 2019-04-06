package ru.hse;

import ru.hse.graphic.LineGraph;
import ru.hse.learning_algorithm.TPMTrainer;
import ru.hse.tree_parity_machine.TreeParityMachine;

import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws Exception {
        TreeParityMachine tpm1 = new TreeParityMachine(8, 4, -2, 2);
        TreeParityMachine tpm2 = new TreeParityMachine(8, 4, -2, 2);
        TPMTrainer trainer = new TPMTrainer();
        ArrayList[] list = trainer.synchronize(tpm1, tpm2);
        System.out.println(list[0] + "\n"+list[1]);
        System.out.println(Arrays.toString(tpm1.getSecretKey()));
        System.out.println(Arrays.toString(tpm2.getSecretKey()));
        new LineGraph().plot(list[0], list[1]);
    }

}
