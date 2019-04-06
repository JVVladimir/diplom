package ru.hse;

import org.junit.jupiter.api.Test;
import ru.hse.tree_parity_machine.NeuralNetException;
import ru.hse.utils.Random;

import java.util.Arrays;

public class Main {

    @Test
    public void testMethod() throws NeuralNetException {
        System.out.println(Arrays.toString(Random.getInts(10, -1, 2)));
       /* TreeParityMachine tpm1 = new TreeParityMachine(8, 4, -2, 2);
        TreeParityMachine tpm2 = new TreeParityMachine(8, 4, -2, 2);
        TPMTrainer trainer = new TPMTrainer();
        trainer.synchronize(tpm1, tpm2);*/
    }

}
