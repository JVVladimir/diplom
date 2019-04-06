public class HiddenNeuron extends Neuron {

    private Random random;

    public HiddenNeuron(int inputs, Random random) {
        this.inputs = inputs;
        weights = new double[inputs];
        this.random = random;
    }

    public void init() {
        for (int i = 0; i < inputs; i++)
            weights[i] = random.getInt();
    }

    @Override
    public void changeWeights(double[] input, int outputTPM) {
        for (int i = 0; i < input.length; i++) {
            double dW = input[i] * outputTPM;
            if (Math.abs(weights[i] + dW) <= random.getRight())
                weights[i] += dW;

        }
    }

    public int getOutput(double[] input)  {
        if (input == null || input.length != inputs)
            System.out.println("Входной вектор не соответствует кол-ву весовых коэффициентов");
        double sum = 0;
        for (int i = 0; i < inputs; i++)
            sum += weights[i] * input[i];
        output = sum > 0 ? 1 : -1;
        return output;
    }

}