package ru.hse.tree_parity_machine.layer;

import ru.hse.learning_algorithm.LearningParadigm;
import ru.hse.tree_parity_machine.NeuralNetException;
import ru.hse.tree_parity_machine.neuron.HiddenNeuron;
import ru.hse.tree_parity_machine.neuron.Neuron;

import java.util.Arrays;

public class HiddenLayer extends NetLayer {

    private short[] res;

    public HiddenLayer(int n, int k, int leftBound, int rightBound, LearningParadigm paradigm) {
        inputs = n;
        outputs = k;
        this.paradigm = paradigm;
        neurons = new Neuron[k];
        for (int i = 0; i < k; i++) {
            neurons[i] = new HiddenNeuron(n, leftBound, rightBound, paradigm);
            neurons[i].init();
        }
    }

    public short[] getOutput(short[] input) {
        if (input.length != inputs * outputs)
            throw new NeuralNetException("Входной вектор не соответствует кол-ву весовых коэффициентов");
        short[] res = new short[outputs];
        for (int i = 0; i < outputs; i++)
            res[i] = neurons[i].getOutput(Arrays.copyOfRange(input, inputs * i, inputs * (i + 1)));
        this.res = res;
        return res;
    }

    @Override
    public String toString() {
        return "HiddenLayer{" +
                "res=" + Arrays.toString(res) +
                ", outputs=" + outputs +
                ", inputs=" + inputs +
                ", neurons=" + Arrays.toString(neurons) +
                ", paradigm=" + paradigm +
                '}';
    }
}
