package ru.hse.tree_parity_machine.layer;

import ru.hse.tree_parity_machine.neuron.HiddenNeuron;
import ru.hse.tree_parity_machine.neuron.Neuron;

import java.util.Arrays;

public class HiddenLayer extends NetLayer {

    private double[] res;

    public HiddenLayer(int n, int k, int leftBound, int rightBound) {
        inputs = n;
        outputs = k;
        neurons = new Neuron[k];
        for (int i = 0; i < k; i++) {
            neurons[i] = new HiddenNeuron(n, leftBound, rightBound);
            neurons[i].init();
        }
    }

    public double[] getOutput(double[] input) {
        if (input.length != inputs) System.out.println("Входной вектор не соответствует кол-ву весовых коэффициентов");
        double[] res = new double[outputs];
        for (int i = 0; i < outputs; i++)
            res[i] = neurons[i].getOutput(input);
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
                '}';
    }
}
