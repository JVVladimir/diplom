public class HiddenLayer extends NetLayer {

    public HiddenLayer(int n, int k, Random random) {
        inputs = n;
        outputs = k;
        neurons = new Neuron[k];
        for (int i = 0; i < k; i++) {
            neurons[i] = new HiddenNeuron(n, random);
            neurons[i].init();
        }
    }

    public double[] getOutput(double[] input) {
        if (input.length != inputs)
            System.out.println("Входной вектор не соответствует кол-ву весовых коэффициентов");
        double[] res = new double[outputs];
        for (int i = 0; i < outputs; i++)
            res[i] = neurons[i].getOutput(input);
        return res;
    }
}
