package ru.hse.learning_algorithm;

import ru.hse.tree_parity_machine.NeuralNetException;
import ru.hse.tree_parity_machine.TreeParityMachine;
import ru.hse.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;

public class TPMTrainer {

    private int numIteration = 100;

    public ArrayList[] synchronize(TreeParityMachine tpm1, TreeParityMachine tpm2) throws NeuralNetException {
        ArrayList<Integer> outputTPM1 = new ArrayList<>();
        ArrayList<Integer> outputTPM2 = new ArrayList<>();
        int[] params = tpm1.getTPMParams();
        double[] input = Random.getIntsCastedToDouble(params[0]);
        double[] input2 = Random.getIntsCastedToDouble(params[0]);
        while (numIteration > 0) {
            int out1 = tpm1.getOutput(input);
            int out2 = tpm2.getOutput(input2);
            //System.out.println(out1 + " : "+out2);
            System.out.println("input: " + Arrays.toString(input));
            System.out.println("input2: " + Arrays.toString(input2));
            System.out.println("key1: "+Arrays.toString(tpm1.getSecretKey()));
            System.out.println("key2: "+Arrays.toString(tpm2.getSecretKey()));
            outputTPM1.add(out1);
            outputTPM2.add(out2);
            if (out1 == out2) {
               // tpm1.train(input);
                tpm2.train(input);
            } else {
                input = Random.getIntsCastedToDouble(params[0]);
            }
            numIteration--;
        }
        return new ArrayList[]{outputTPM1, outputTPM2};
    }

    public int getNumIteration() {
        return numIteration;
    }

    public void setNumIteration(int numIteration) {
        this.numIteration = numIteration;
    }
}
