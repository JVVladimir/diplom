public abstract class Neuron {

    protected int output;
    protected int inputs;
    protected double[] weights;


    public abstract void init();

    public abstract void changeWeights(double[] input, int outputTPM);

    public abstract int getOutput(double[] input);

    public double[] getWeights() {
        return weights;
    }

}
