package ru.hse;

import org.junit.jupiter.api.Test;
import ru.hse.learning_algorithm.TPMTrainer;
import ru.hse.tree_parity_machine.NeuralNetException;
import ru.hse.tree_parity_machine.TreeParityMachine;

public class Main {

    @Test
    public void testMethod() throws NeuralNetException {
        TreeParityMachine tpm1 = new TreeParityMachine(8, 4, -2, 2);
        TreeParityMachine tpm2 = new TreeParityMachine(8, 4, -2, 2);
        TPMTrainer trainer = new TPMTrainer();
        trainer.synchronize(tpm1, tpm2);
    }

    @Test
    public void testMethod2() throws NeuralNetException {
        TreeParityMachine tpm1 = new TreeParityMachine(8, 4, -2, 2);
        TreeParityMachine tpm2 = new TreeParityMachine(8, 4, -2, 2);
        TPMTrainer trainer = new TPMTrainer();
        trainer.synchronize(tpm1, tpm2);
    }

}
