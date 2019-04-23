package ru.hse;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.hse.learning_algorithm.LearningParadigm;
import ru.hse.learning_algorithm.TPMTrainer;
import ru.hse.tree_parity_machine.TreeParityMachine;
import ru.hse.utils.Random;

public class TestTPM {

    // key = 256 bit   8*16 = 128 + *2 т.к. берём два бита с каждого числа
    // byte arduino 0 - 255 - чистые 8 бит
    //

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
        System.out.println(weightToKey(t1.getSecretKey(), (short) 32, 8, (short) 2));
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
