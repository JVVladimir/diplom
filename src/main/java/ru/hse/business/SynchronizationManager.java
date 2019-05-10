package ru.hse.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hse.arduino.ArduinoController;
import ru.hse.arduino.Controller;
import ru.hse.business.entity.RequestData;

public abstract class SynchronizationManager implements Handler {

    private static final Logger log = LoggerFactory.getLogger(SynchronizationManager.class);

    protected Controller controller;

    protected volatile boolean isSync;
    protected int epochs;
    protected int maxEpochs = 150;
    protected int inputs;
    protected short out;

    protected int curCommand;

    protected static final int NOP = 0;
    protected static int INIT_W;
    protected static final int INIT_X = 2;
    protected static final int TRAIN = 3;
    protected static final int SYNC_DONE = 4;

    protected short[] key;

    protected volatile boolean taskDone;
    protected RequestData requestData;

    // TODO: сделать автоопределение подключённых портов (Надо будет на GUI вызвать функцию определения всех портов и из списка их выбирать)
    protected SynchronizationManager(int tpmInputs, int mode) {
        INIT_W = mode;
        this.inputs = tpmInputs;
        this.controller = new ArduinoController(this, ArduinoController.getConnectedComPorts()[0], 115200);
    }

    public abstract RequestData initWeights();

    public abstract RequestData initInput();

    public abstract RequestData train(RequestData requestData);

    public abstract RequestData syncDone();

    protected boolean validateRequestData(RequestData requestData) {
        log.info("Current command: {},  data received: {}", curCommand, requestData);
        if (!requestData.isOk()) {
            log.error("Bad response from Controller no Ok code");
            return false;
        }
        if (curCommand == INIT_W || curCommand == SYNC_DONE)
            return true;
        if (!requestData.vecHasLen(inputs)) {
            log.error("Bad response from Controller no len");
            return false;
        }
        return true;
    }

    // TODO: подумать не переделать ли под поток, возвращающий результат задачи
    protected void waitTask() {
        while (!taskDone)
            Thread.yield();
        taskDone = false;
    }

    public short[] getKey() {
        return key;
    }

    public synchronized boolean isSync() {
        return isSync;
    }

    public int getCurCommand() {
        return curCommand;
    }

    public void destroy() {
        controller.closePort();
    }

    public int getEpochs() {
        return epochs;
    }

    public int getMaxEpochs() {
        return maxEpochs;
    }
}
