
public class Main {

    public static void main(String[] args)  {
        Random random = new Random();
        random.setBounds(-2, 2);
        TreeParityMachine tpm1 = new TreeParityMachine(8, 4, random);
        TreeParityMachine tpm2 = new TreeParityMachine(8, 4, random);
        TPMTrainer trainer = new TPMTrainer(random);
        trainer.synchronize(tpm1, tpm2);

    }

}
