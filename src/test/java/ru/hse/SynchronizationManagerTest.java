package ru.hse;

import org.junit.jupiter.api.*;
import ru.hse.business.SynchronizationManager;
import ru.hse.learning_algorithm.LearningParadigm;
import ru.hse.tree_parity_machine.TreeParityMachine;

public class SynchronizationManagerTest {

    private TreeParityMachine tpm1;
    private SynchronizationManager manager;


    @BeforeEach
    public void init() throws InterruptedException {
        tpm1 = new TreeParityMachine(8, 16, -2, 2, LearningParadigm.HEBBIAN);
        manager = new SynchronizationManager(tpm1);
        Thread.sleep(2000);
    }

    @Test
    @RepeatedTest(200)
    void testSynchronizationManager() {
        manager.generateKey();
        while (!manager.isSync()) {
            try {
                Thread.currentThread().sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Assertions.assertArrayEquals(tpm1.getSecretKey(), manager.getKey(), "Ключи не равны!");
    }

    @AfterEach
    public void destroy() {
        manager.destroy();
    }
}
