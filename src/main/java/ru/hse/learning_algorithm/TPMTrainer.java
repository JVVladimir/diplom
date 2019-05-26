package ru.hse.learning_algorithm;

import ru.hse.tree_parity_machine.NeuralNetException;
import ru.hse.tree_parity_machine.TreeParityMachine;
import ru.hse.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;

public class TPMTrainer {

    private int epochs = 200;

    public ArrayList<Short>[] synchronize(TreeParityMachine tpm1, TreeParityMachine tpm2) {
        if (tpm1.getLearningParadigm() != tpm2.getLearningParadigm())
            throw new NeuralNetException("Алгоритмы обучения не совпадают!");
        short k = 0;
        ArrayList<Short> result = new ArrayList<>();
        ArrayList<Short> outputTPM1 = new ArrayList<>();
        ArrayList<Short> outputTPM2 = new ArrayList<>();
        int[] params = tpm1.getTPMParams();
        short[] input = Random.getInts(params[0] * params[1], -1, 1);
        while (k < epochs) {
            short out1 = tpm1.getOutput(input);
            short out2 = tpm2.getOutput(input);
            if (Arrays.equals(tpm1.getSecretKey(), tpm2.getSecretKey())) {
                result.add(k);
                break;
            }
            outputTPM1.add(out1);
            outputTPM2.add(out2);
            if (out1 != out2) ;
            else {
                tpm1.train(input, out2);
                tpm2.train(input, out1);
            }
            input = Random.getInts(params[0] * params[1], -1, 1);
            k++;
        }
        if (k > epochs)
            throw new NeuralNetException("ДМЧ не синхронизировались!");
        return new ArrayList[]{outputTPM1, outputTPM2, result};
    }

    public void synchronize(TreeParityMachine tpm1, short[] input, short out2) {
        short out1 = tpm1.getOutput(input);
        if (out1 == out2)
            tpm1.train(input, out2);
    }

    public int getEpochs() {
        return epochs;
    }

    public void setEpochs(int epochs) {
        this.epochs = epochs;
    }
}
