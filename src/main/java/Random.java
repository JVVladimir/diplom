public class Random {

    private double left = 0;
    private double right = 0;

    public Random() {
    }

    public int getInt() {
        if (left == 0 && right == 0)
            return new java.util.Random().nextInt();
        else if (left == right)
            return (int) left;
        else
            return (int) (left + Math.round(new java.util.Random().nextDouble() * (right - left)));
    }

    public double[] getIntsCastedToDouble(int n) {
        double[] mas = new double[n];
        for (int i = 0; i < n; i++) {
            if (new java.util.Random().nextDouble() >= 0.5)
                mas[i] = -1;
            else
                mas[i] = 1;
        }
        return mas;
    }

    public void setBounds(double leftBound, double rightBound) {
        left = leftBound;
        right = rightBound;
    }

    public double getRight() {
        return right;
    }
}
