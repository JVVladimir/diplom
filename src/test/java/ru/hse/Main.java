package ru.hse;

import org.junit.jupiter.api.Test;
import ru.hse.learning_algorithm.TPMTrainer;
import ru.hse.tree_parity_machine.NeuralNetException;
import ru.hse.tree_parity_machine.TreeParityMachine;
import ru.hse.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    @Test
    public void testMethod() throws NeuralNetException {
        // System.out.println(Arrays.toString(Random.getInts(10, -1, 2)));
        TreeParityMachine tpm1 = new TreeParityMachine(8, 4, -2, 2);
        TreeParityMachine tpm2 = new TreeParityMachine(8, 4, -2, 2);
        TPMTrainer trainer = new TPMTrainer();
        ArrayList[] list = trainer.synchronize(tpm1, tpm2);
        System.out.println(list[0] + "\n"+list[1]);
        System.out.println(Arrays.toString(tpm1.getSecretKey()));
        System.out.println(Arrays.toString(tpm2.getSecretKey()));
    }

}
