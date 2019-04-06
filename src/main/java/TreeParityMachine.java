public class TreeParityMachine  {

    private int n;
    private int k;
    private HiddenLayer hiddenLayer;
    private OutputLayer outputLayer;

    public TreeParityMachine(int n, int k, Random random) {
        this.n = n;
        this.k = k;
        hiddenLayer = new HiddenLayer(n, k, random);
        outputLayer = new OutputLayer(k);
    }

    public int getOutput(double[] input) {
        return outputLayer.getOutput(hiddenLayer.getOutput(input));
    }


    public void train(double[] input, int output) {

        double[] hiddenOutput = hiddenLayer.getOutput(input);
        Neuron[] hiddenNeurons = hiddenLayer.getNeurons();
        for (int i = 0; i < hiddenOutput.length; i++)
            hiddenNeurons[i].changeWeights(input, output);

    }

    public double[] getSecretKey() {
        double[] key = new double[n * k];
        Neuron[] neurons = hiddenLayer.getNeurons();
        for (int i = 0; i < k; i++) {
            double[] mas = neurons[i].getWeights();
            for (int j = 0; j < n; j++)
                key[i * n + j] = mas[j];
        }
        return key;
    }


    public int[] getTPMParams() {
        int[] params = {n, k};
        return params;
    }

}
