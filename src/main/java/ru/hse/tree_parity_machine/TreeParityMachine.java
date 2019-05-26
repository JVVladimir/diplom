package ru.hse.tree_parity_machine;

import ru.hse.learning_algorithm.LearningParadigm;
import ru.hse.learning_algorithm.Training;
import ru.hse.tree_parity_machine.layer.HiddenLayer;
import ru.hse.tree_parity_machine.layer.OutputLayer;
import ru.hse.tree_parity_machine.neuron.Neuron;

import java.util.Arrays;

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
        Neuron[] hiddenNeurons = hiddenLayer.getNeurons();
        for (int i = 0; i < k; i++)
            hiddenNeurons[i].changeWeights(Arrays.copyOfRange(input, n * i, n * (i + 1)), output);
    }

    public short[] getSecretKey() {
        short[] key = new short[n * k];
        Neuron[] neurons = hiddenLayer.getNeurons();
        for (int i = 0; i < k; i++) {
            short[] mas = neurons[i].getWeights();
            if (n >= 0) System.arraycopy(mas, 0, key, i * n, n);
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
        return new int[]{n, k};
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
