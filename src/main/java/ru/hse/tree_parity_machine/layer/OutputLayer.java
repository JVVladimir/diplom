package ru.hse.tree_parity_machine.layer;

import ru.hse.tree_parity_machine.NeuralNetException;
import ru.hse.tree_parity_machine.neuron.Neuron;
import ru.hse.tree_parity_machine.neuron.OutputNeuron;

public class OutputLayer extends NetLayer {

    public OutputLayer(int k) {
        inputs = k;
        outputs = 1;
        neurons = new Neuron[1];
        neurons[0] = new OutputNeuron(k);
        neurons[0].init();
    }

    public int getOutput(double[] input) {
        if (input.length != inputs)
            throw new NeuralNetException("Входной вектор не соответствует числу нейронов на скрытом слое");
        return neurons[0].getOutput(input);
    }
}
