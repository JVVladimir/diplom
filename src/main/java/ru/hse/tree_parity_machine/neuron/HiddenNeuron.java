package ru.hse.tree_parity_machine.neuron;

import ru.hse.learning_algorithm.LearningParadigm;
import ru.hse.tree_parity_machine.NeuralNetException;
import ru.hse.utils.Random;

public class HiddenNeuron extends Neuron {

    public HiddenNeuron(int inputs, int leftBound, int rightBound, LearningParadigm paradigm) {
        this.inputs = inputs;
        this.paradigm = paradigm;
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
            switch (paradigm) {
                case HEBIAN:
                    if (Math.abs(weights[i] + dW) <= rightBound)
                        weights[i] += input[i] * outputTPM;
                    //else
                      //  weights[i] -= input[i] * outputTPM;
                    break;
                case ANTI_HEBBIAN:
                    if (Math.abs(weights[i] + dW) <= rightBound)
                        weights[i] -= input[i] * outputTPM;
                    else
                        weights[i] += input[i] * outputTPM;
                    break;
                case RANDOM_WALK:
                    if (Math.abs(weights[i] + dW) <= rightBound)
                        weights[i] += input[i];
                    else
                        weights[i] -= input[i] * outputTPM;
                    break;
            }
        }
    }

    public int getOutput(double[] input) throws NeuralNetException {
        if (input == null || input.length != inputs)
            throw new NeuralNetException("Входной вектор не соответствует кол-ву весовых коэффициентов");
        double sum = 0;
        for (int i = 0; i < inputs; i++)
            sum += weights[i] * input[i];
        output = sum > 0 ? 1 : -1;
        return output;
    }

}
