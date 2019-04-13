package ru.hse;

import ru.hse.business.SynchronizationManager;
import ru.hse.learning_algorithm.LearningParadigm;
import ru.hse.tree_parity_machine.TreeParityMachine;

public class Main {

    public static void main(String[] args) {
        TreeParityMachine tpm1 = new TreeParityMachine(16, 16, -8, 8, LearningParadigm.HEBBIAN);
        SynchronizationManager manager = new SynchronizationManager(tpm1);
        manager.handleResponse(new byte[] {SynchronizationManager.INIT_W});


    }

}
