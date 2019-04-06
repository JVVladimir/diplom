package ru.hse.tree_parity_machine.neuron;

import ru.hse.utils.Random;

public class HiddenNeuron extends Neuron {

    public HiddenNeuron(int inputs, int leftBound, int rightBound) {
        this.inputs = inputs;
        weights = new double[inputs];
        this.leftBound = leftBound;
        this.rightBound = rightBound;
        Random.setBounds(leftBound, rightBound);
    }

    public void init() {
        for (int i = 0; i < inputs; i++)
            weights[i] = Random.getInt();
    }

    @Override
    public void changeWeights(double[] input, int outputTPM) {
        for (int i = 0; i < input.length; i++) {
            double dW = input[i] * outputTPM;
            if (Math.abs(weights[i] + dW) <= rightBound) weights[i] += dW;
        }
    }

    public int getOutput(double[] input) {
        if (input == null || input.length != inputs)
            System.out.println("Входной вектор не соответствует кол-ву весовых коэффициентов");
        double sum = 0;
        for (int i = 0; i < inputs; i++)
            sum += weights[i] * input[i];
        output = sum > 0 ? 1 : -1;
        return output;
    }

}