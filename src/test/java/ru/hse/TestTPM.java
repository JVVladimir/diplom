package ru.hse;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.hse.business.LeadSynchronizationManager;
import ru.hse.business.SlaveSynchronizationManager;
import ru.hse.business.SynchronizationManager;
import ru.hse.business.entity.RequestData;
import ru.hse.learning_algorithm.LearningParadigm;
import ru.hse.learning_algorithm.TPMTrainer;
import ru.hse.tree_parity_machine.TreeParityMachine;
import ru.hse.utils.Random;

import java.util.Arrays;

public class TestTPM {

    @Test
    void testTpm() {
        int epochs = 150, i = 0;
        TreeParityMachine t1 = new TreeParityMachine(8, 16, -2, 2, LearningParadigm.HEBBIAN);
        TreeParityMachine t2 = new TreeParityMachine(8, 16, -2, 2, LearningParadigm.HEBBIAN);
        int[] p = t1.getTPMParams();
        TPMTrainer trainer = new TPMTrainer();
        short[] input = Random.getInts(p[0] * p[1], -1, 1);
        while (i < epochs) {
            i++;
            short out2 = t2.getOutput(input);
            short out1 = t1.getOutput(input);
            trainer.synchronize(t1, input, out2);

            //out1 = t1.getOutput(input);

            trainer.synchronize(t2, input, out1);

            input = Random.getInts(p[0] * p[1], -1, 1);
        }
        System.out.println(Arrays.toString(t1.getSecretKey()));
        System.out.println(Arrays.toString(t2.getSecretKey()));
        Assertions.assertArrayEquals(t1.getSecretKey(), t2.getSecretKey(), "ДМЧ не синхронизировались!");
        //System.out.println(weightToKey(t1.getSecretKey(), (short) 32, 8, (short) 2));
    }

    @Test
    void testArduinoSlave() throws InterruptedException {
        TreeParityMachine tpm1 = new TreeParityMachine(8, 14, -2, 2, LearningParadigm.HEBBIAN);
        int[] p = tpm1.getTPMParams();
        System.out.println(Arrays.toString(p));
        TPMTrainer trainer = new TPMTrainer();

        SynchronizationManager manager = new SlaveSynchronizationManager();

        int i = 0;
        Thread.sleep(2000);
        manager.initWeights();
        short[] input = Random.getInts(p[0] * p[1], -1, 1);

        while (i < 80) {
            i++;
            RequestData requestData = manager.train(new RequestData(input, tpm1.getOutput(input), 0, 0, null));
            short out = requestData.getOut();
            System.out.print(out + "   ");
            System.out.println(tpm1.getOutput(input));
            trainer.synchronize(tpm1, input, out);
            // System.out.println(tpm1.getOutput(input));
            // manager.setInput(requestData.getInput());
            input = Random.getInts(p[0] * p[1], -1, 1);

            // System.out.println(tpm1.getOutput(input));
            manager.setOut2(tpm1.getOutput(input));
            manager.setInput(input);
        }
        RequestData data = manager.syncDone();
        Assertions.assertArrayEquals(tpm1.getSecretKey(), data.getWeight(), "ДМЧ не синхронизировались!");
        // System.out.println(Arrays.toString(manager.getKey()));
    }

    @Test
    void testArduinoLead() throws InterruptedException {
        TreeParityMachine tpm1 = new TreeParityMachine(8, 8, -2, 2, LearningParadigm.HEBBIAN);
        int[] p = tpm1.getTPMParams();
        TPMTrainer trainer = new TPMTrainer();

        SynchronizationManager manager = new LeadSynchronizationManager();

        int i = 0;
        Thread.sleep(2000);
        manager.initWeights();
        RequestData requestData = manager.initInput();
        while (i < 80) {
            i++;
            trainer.synchronize(tpm1, requestData.getInput(), requestData.getOut());
            requestData = manager.train(new RequestData(null, tpm1.getOutput(requestData.getInput()), 0, 0, null));
            manager.setInput(requestData.getInput());
            manager.setOut2(tpm1.getOutput(manager.getInput()));
        }
        RequestData data = manager.syncDone();
        Assertions.assertArrayEquals(tpm1.getSecretKey(), data.getWeight(), "ДМЧ не синхронизировались!");

        // System.out.println(Arrays.toString(manager.getKey()));
    }

    char[] weightToKey(short[] key, short lenKey, int typeLen, short l) {
        int c = 0;
        while (l != 0) {
            c++;
            l >>= 1;
        }
        short shift = (short) c;
        System.out.println(shift);
        // shift - должен быть степенью двойки
        int limit = typeLen / shift - 1;
        char[] mas = new char[lenKey];
        for (int i = 0, j = 0, k = 0; i < lenKey; i++) {
            while (k != limit) {
                mas[i] |= key[j];
                j++;
                mas[i] <<= shift;
                k++;
            }
            mas[i] |= key[j];
            k = 0;
        }
        return mas;
    }
}
