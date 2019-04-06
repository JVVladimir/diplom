package ru.hse.tree_parity_machine;

import ru.hse.tree_parity_machine.layer.HiddenLayer;
import ru.hse.tree_parity_machine.layer.OutputLayer;
import ru.hse.tree_parity_machine.neuron.Neuron;

public class TreeParityMachine {

    private int n;
    private int k;
    private int leftBound;
    private int rightBound;
    private HiddenLayer hiddenLayer;
    private OutputLayer outputLayer;

    public TreeParityMachine(int n, int k, int leftBound, int rightBound) {
        this.n = n;
        this.k = k;
        this.leftBound = leftBound;
        this.rightBound = rightBound;
        hiddenLayer = new HiddenLayer(n, k, leftBound, rightBound);
        outputLayer = new OutputLayer(k);
    }

    public int getOutput(double[] input) {
        return outputLayer.getOutput(hiddenLayer.getOutput(input));
    }

    public void train(double[] input, int output) {
        double[] hiddenOutput = hiddenLayer.getOutput(input);
        Neuron[] hiddenNeurons = hiddenLayer.getNeurons();
        for (int i = 0; i < hiddenOutput.length; i++)
            hiddenNeurons[i].changeWeights(input, output);
    }

    public double[] getSecretKey() {
        double[] key = new double[n * k];
        Neuron[] neurons = hiddenLayer.getNeurons();
        for (int i = 0; i < k; i++) {
            double[] mas = neurons[i].getWeights();
            for (int j = 0; j < n; j++)
                key[i * n + j] = mas[j];
        }
        return key;
    }

    public int[] getTPMParams() {
        int[] params = {n, k};
        return params;
    }

    @Override
    public String toString() {
        return "TreeParityMachine{" +
                "n=" + n +
                ", k=" + k +
                ", leftBound=" + leftBound +
                ", rightBound=" + rightBound +
                ", hiddenLayer=" + hiddenLayer +
                ", outputLayer=" + outputLayer +
                '}';
    }
}
