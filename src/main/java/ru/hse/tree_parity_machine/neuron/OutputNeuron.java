package ru.hse.tree_parity_machine.neuron;

import ru.hse.tree_parity_machine.NeuralNetException;

/***/
public class OutputNeuron extends Neuron {

    public OutputNeuron(int inputs) {
        this.inputs = inputs;
        weights = new double[inputs];
    }

    public void init() {
        for (int i = 0; i < inputs; i++)
            weights[i] = 1;
    }

    @Override
    public void changeWeights(double[] input, int outputTPM) {

    }

    public int getOutput(double[] input) throws NeuralNetException {
        if (input == null || input.length != inputs)
            throw new NeuralNetException("Входной вектор не соответствует кол-ву весовых коэффициентов");
        int res = 1;
        for (int i = 0; i < inputs; i++)
            res *= input[i];
        output = res;
        return res;
    }

}