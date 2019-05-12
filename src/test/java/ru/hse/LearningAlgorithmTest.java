package ru.hse;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import ru.hse.learning_algorithm.LearningParadigm;
import ru.hse.learning_algorithm.TPMTrainer;
import ru.hse.tree_parity_machine.NeuralNetException;
import ru.hse.tree_parity_machine.TreeParityMachine;

import java.util.ArrayList;
import java.util.Arrays;

public class LearningAlgorithmTest {

    static float numHebbian;
    static float numAntiHebbian;
    static float numRandomWalk;
    static float num1;
    static float num2;
    static float num3;

    static int numHebbianFails;
    static int numAntiHebbianFails;
    static int numRandomWalkFails;

    static int epochs = 1500;
    int L = 2, n = 8, k = 16;


    @AfterAll
    public static void setup() {

    }

    @Test
    @RepeatedTest(1)
    public void testHebbian() throws NeuralNetException {
        try {
            TreeParityMachine tpm1 = new TreeParityMachine(n, k, -L, L, LearningParadigm.HEBBIAN);
            TreeParityMachine tpm2 = new TreeParityMachine(n, k, -L, L, LearningParadigm.HEBBIAN);
            TPMTrainer trainer = new TPMTrainer();
            trainer.setEpochs(epochs);
            ArrayList[] list = trainer.synchronize(tpm1, tpm2);
            System.out.println("\n\n");
            numHebbian += (Integer) list[2].get(0);
            num1++;
        } catch (Exception ex) {
            numHebbianFails++;
        }
    }

    @Test
    @RepeatedTest(1)
    public void testAntiHebbian() throws NeuralNetException {
        try {
            TreeParityMachine tpm1 = new TreeParityMachine(n, k, -L, L, LearningParadigm.ANTI_HEBBIAN);
            TreeParityMachine tpm2 = new TreeParityMachine(n, k, -L, L, LearningParadigm.ANTI_HEBBIAN);
            TPMTrainer trainer = new TPMTrainer();
            trainer.setEpochs(epochs);
            ArrayList[] list = trainer.synchronize(tpm1, tpm2);
            numAntiHebbian += (Integer) list[2].get(0);
            num2++;
        } catch (Exception ex) {
            numAntiHebbianFails++;
        }
    }

    @Test
    @RepeatedTest(1)
    public void testRandomWalk() throws NeuralNetException {
        try {
            TreeParityMachine tpm1 = new TreeParityMachine(n, k, -L, L, LearningParadigm.RANDOM_WALK);
            TreeParityMachine tpm2 = new TreeParityMachine(n, k, -L, L, LearningParadigm.RANDOM_WALK);
            TPMTrainer trainer = new TPMTrainer();
            trainer.setEpochs(epochs);
            ArrayList[] list = trainer.synchronize(tpm1, tpm2);
            numRandomWalk += (Integer) list[2].get(0);
            num3++;
        } catch (Exception ex) {
            numRandomWalkFails++;
        }
    }

    @AfterAll
    public static void result() {
        System.out.printf("*** Статистика на %s запусков с макс. числом эпох: %d ***\n", 1000, epochs);
        System.out.printf("Среднее для Хебба:%38.2f итер.\n", numHebbian / num1);
        System.out.printf("Среднее для Анти-Хебба:%33.2f итер.\n", numAntiHebbian / num2);
        System.out.printf("Среднее для Random walk:%32.2f итер.\n", numRandomWalk / num3);
        System.out.printf("Кол-во падений для Хебба:%27d\n", numHebbianFails);
        System.out.printf("Кол-во падений для Анти-Хебба:%22d\n", numAntiHebbianFails);
        System.out.printf("Кол-во падений для Random walk:%21d\n", numRandomWalkFails);
        System.out.printf("Процент падений для Хебба от общего кол-ва:%14.1f \n", ((float) numHebbianFails) / 1000 * 100);
        System.out.printf("Процент падений для Анти-Хебба от общего кол-ва:%9.1f \n", ((float) numAntiHebbianFails) / 1000 * 100);
        System.out.printf("Процент падений для Random walk от общего кол-ва:%8.1f \n", ((float) numRandomWalkFails) / 1000 * 100);
    }

}
