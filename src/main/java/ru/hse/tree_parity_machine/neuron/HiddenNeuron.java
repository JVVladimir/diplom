package ru.hse.tree_parity_machine.neuron;

import ru.hse.learning_algorithm.LearningParadigm;
import ru.hse.tree_parity_machine.NeuralNetException;
import ru.hse.utils.Random;

public class HiddenNeuron extends Neuron {

    public HiddenNeuron(int inputs, int leftBound, int rightBound, LearningParadigm paradigm) {
        this.inputs = inputs;
        this.paradigm = paradigm;
        weights = new int[inputs];
        this.leftBound = leftBound;
        this.rightBound = rightBound;
    }

    public void init() {
        for (int i = 0; i < inputs; i++)
            weights[i] = Random.getInt(leftBound, rightBound);
    }

    @Override
    public void changeWeights(int[] input, int outputTPM) {
        for (int i = 0; i < input.length; i++) {
            int dW = input[i] * outputTPM;
            switch (paradigm) {
                case HEBIAN:
                    weights[i] += dW;
                    break;
                case ANTI_HEBBIAN:
                    weights[i] -= dW;
                    break;
                case RANDOM_WALK:
                    weights[i] += input[i];
                    break;
            }
            if (weights[i] > rightBound)
                weights[i] = rightBound;
            else if (weights[i] < leftBound)
                weights[i] = leftBound;
        }
    }

    public int getOutput(int[] input) {
        if (input == null || input.length != inputs)
            throw new NeuralNetException("Входной вектор не соответствует кол-ву весовых коэффициентов");
        int sum = 0;
        for (int i = 0; i < inputs; i++)
            sum += weights[i] * input[i];
        output = sum > 0 ? 1 : -1;
        return output;
    }

}