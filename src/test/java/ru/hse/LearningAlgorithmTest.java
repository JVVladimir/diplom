package ru.hse;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import ru.hse.learning_algorithm.LearningParadigm;
import ru.hse.learning_algorithm.TPMTrainer;
import ru.hse.tree_parity_machine.NeuralNetException;
import ru.hse.tree_parity_machine.TreeParityMachine;

import java.util.ArrayList;

public class LearningAlgorithmTest {

    static double numHebbian;
    static double numAntiHebbian;
    static double numRandomWalk;

    @Test
    @RepeatedTest(10000)
    public void testHebbian() throws NeuralNetException {
        TreeParityMachine tpm1 = new TreeParityMachine(8, 4, -2, 2, LearningParadigm.HEBIAN);
        TreeParityMachine tpm2 = new TreeParityMachine(8, 4, -2, 2, LearningParadigm.HEBIAN);
        TPMTrainer trainer = new TPMTrainer();
        ArrayList[] list = trainer.synchronize(tpm1, tpm2);
        numHebbian += (Integer) list[2].get(0);
    }

    @Test
    @RepeatedTest(10000)
    public void testAntiHebbian() throws NeuralNetException {
        TreeParityMachine tpm1 = new TreeParityMachine(8, 4, -2, 2, LearningParadigm.ANTI_HEBBIAN);
        TreeParityMachine tpm2 = new TreeParityMachine(8, 4, -2, 2, LearningParadigm.ANTI_HEBBIAN);
        TPMTrainer trainer = new TPMTrainer();
        ArrayList[] list = trainer.synchronize(tpm1, tpm2);
        numAntiHebbian += (Integer) list[2].get(0);
    }

    @Test
    @RepeatedTest(10000)
    public void testRandomWalk() throws NeuralNetException {
        TreeParityMachine tpm1 = new TreeParityMachine(8, 4, -2, 2, LearningParadigm.RANDOM_WALK);
        TreeParityMachine tpm2 = new TreeParityMachine(8, 4, -2, 2, LearningParadigm.RANDOM_WALK);
        TPMTrainer trainer = new TPMTrainer();
        ArrayList[] list = trainer.synchronize(tpm1, tpm2);
        numRandomWalk += (Integer) list[2].get(0);
    }

    @AfterAll
    public static void result() {
        System.out.println("Среднее для Хебба:   " + numHebbian / 10000);
        System.out.println("Среднее для Анти-Хебба:   " + numAntiHebbian / 10000);
        System.out.println("Среднее для Random walk:   " + numRandomWalk / 10000);
    }

}
