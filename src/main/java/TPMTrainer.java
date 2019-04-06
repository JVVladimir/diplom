import java.util.ArrayList;


public class TPMTrainer {

    private int numIteration = 200;
    private Random random;

    public TPMTrainer(Random random) {
        this.random = random;
    }

    public ArrayList<Integer>[] synchronize(TreeParityMachine tpm1, TreeParityMachine tpm2) {
        int k = 0;
        ArrayList<Integer> result = new ArrayList<>();
        ArrayList<Integer> outputTPM1 = new ArrayList<>();
        ArrayList<Integer> outputTPM2 = new ArrayList<>();
        int[] params = tpm1.getTPMParams();
        double[] input = random.getIntsCastedToDouble(params[0]);
        while (k < numIteration) {
            int out1 = tpm1.getOutput(input);
            int out2 = tpm2.getOutput(input);
            if(equalArr(tpm1.getSecretKey(), tpm2.getSecretKey())) {
                result.add(k);
                break;
            }
            outputTPM1.add(out1);
            outputTPM2.add(out2);
            if (out1 != out2) ;
            else {
                tpm1.train(input, out2);
                tpm2.train(input, out1);
            }
            input = random.getIntsCastedToDouble(params[0]);
            k++;
        }
        return new ArrayList[]{outputTPM1, outputTPM2, result};
    }

    boolean equalArr(double[] ar1, double[] ar2) {

        if (ar1.length != ar2.length)
            return false;

        for (int i = 0; i<ar1.length; i++) {
            if (ar1[i]!=ar2[i])
                return false;
        }
        return true;
    }

}
