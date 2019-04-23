package ru.hse;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.hse.learning_algorithm.LearningParadigm;
import ru.hse.learning_algorithm.TPMTrainer;
import ru.hse.tree_parity_machine.TreeParityMachine;
import ru.hse.utils.Random;

public class TestTPM {

    @Test
    void testTpm() {
        int epochs = 150, i = 0;
        TreeParityMachine t1 = new TreeParityMachine(8, 16, -2, 2, LearningParadigm.HEBBIAN);
        TreeParityMachine t2 = new TreeParityMachine(8, 16, -2, 2, LearningParadigm.HEBBIAN);
        TPMTrainer trainer = new TPMTrainer();
        while (i < epochs) {
            i++;
            short[] input = Random.getInts(8, -2, 2);
            short out2 = t2.getOutput(input);
            short out1 = trainer.synchronize(t1, input, out2);
            trainer.synchronize(t2, input, out1);
        }
        Assertions.assertArrayEquals(t1.getSecretKey(), t2.getSecretKey(), "ДМЧ не синхронизировались!");
    }

}
