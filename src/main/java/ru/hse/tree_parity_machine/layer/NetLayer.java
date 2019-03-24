package ru.hse.tree_parity_machine.layer;

import ru.hse.learning_algorithm.LearningParadigm;
import ru.hse.tree_parity_machine.neuron.Neuron;

import java.util.Arrays;

public abstract class NetLayer {

    protected int outputs;
    protected int inputs;
    protected Neuron[] neurons;
    protected LearningParadigm paradigm;

    public Neuron[] getNeurons() {
        return neurons;
    }

    public LearningParadigm getParadigm() {
        return paradigm;
    }

    public void setParadigm(LearningParadigm paradigm) {
        this.paradigm = paradigm;
    }

    @Override
    public String toString() {
        return "NetLayer{" +
                "neurons=" + Arrays.toString(neurons) +
                ", outputs=" + outputs +
                ", inputs=" + inputs +
                '}';
    }
}
