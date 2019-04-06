package ru.hse.tree_parity_machine.layer;

import ru.hse.tree_parity_machine.neuron.Neuron;
import ru.hse.tree_parity_machine.neuron.OutputNeuron;

public class OutputLayer extends NetLayer {

    public OutputLayer(int k) {
        inputs = k;
        outputs = 1;
        neurons = new Neuron[1];
        for(int i = 0; i < 1; i++) {
            neurons[i] = new OutputNeuron(k);
            neurons[i].init();
        }
    }

    public int getOutput(double[] input) {
        if(input.length != inputs)
            System.out.println("Входной вектор не соответствует числу нейронов на скрытом слое");
        return neurons[0].getOutput(input);
    }
}
