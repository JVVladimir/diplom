package ru.hse.tree_parity_machine;

import ru.hse.learning_algorithm.LearningParadigm;
import ru.hse.learning_algorithm.Training;
import ru.hse.tree_parity_machine.layer.HiddenLayer;
import ru.hse.tree_parity_machine.layer.OutputLayer;
import ru.hse.tree_parity_machine.neuron.Neuron;

public class TreeParityMachine implements Training {

    private int n;
    private int k;
    private int leftBound;
    private int rightBound;
    private HiddenLayer hiddenLayer;
    private OutputLayer outputLayer;
    private LearningParadigm paradigm;

    public TreeParityMachine(int n, int k, int leftBound, int rightBound, LearningParadigm learningParadigm) {
        this.n = n;
        this.k = k;
        this.leftBound = leftBound;
        this.rightBound = rightBound;
        this.paradigm = learningParadigm;
        hiddenLayer = new HiddenLayer(n, k, leftBound, rightBound, paradigm);
        outputLayer = new OutputLayer(k);
    }

    public short getOutput(short[] input) {
        return outputLayer.getOutput(hiddenLayer.getOutput(input));
    }

    @Override
    public void train(short[] input, short output) {
        short[] hiddenOutput = hiddenLayer.getOutput(input);
        Neuron[] hiddenNeurons = hiddenLayer.getNeurons();
        for (int i = 0; i < hiddenOutput.length; i++)
            hiddenNeurons[i].changeWeights(input, output);
    }

    public int[] getSecretKey() {
        int[] key = new int[n * k];
        Neuron[] neurons = hiddenLayer.getNeurons();
        for (int i = 0; i < k; i++) {
            int[] mas = neurons[i].getWeights();
            for (int j = 0; j < n; j++)
                key[i * n + j] = mas[j];
        }
        return key;
    }

    public LearningParadigm getLearningParadigm() {
        return paradigm;
    }

    public void setLearningParadigm(LearningParadigm paradigm) {
        this.paradigm = paradigm;
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
                ", paradigm=" + paradigm +
                '}';
    }
}
