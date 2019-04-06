package ru.hse.tree_parity_machine.neuron;


import java.util.Arrays;

public abstract class Neuron {

    protected int output;
    protected int inputs;
    protected double[] weights;
    protected int leftBound;
    protected int rightBound;

    public abstract void init();

    public abstract void changeWeights(double[] input, int outputTPM);

    public abstract int getOutput(double[] input);

    public double[] getWeights() {
        return weights;
    }

    @Override
    public String toString() {
        return "Neuron{" +
                "inputs=" + inputs +
                ", weights=" + Arrays.toString(weights) +
                '}';
    }
}
