package ru.hse.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hse.arduino.ArduinoController;
import ru.hse.arduino.Controller;
import ru.hse.business.entity.RequestData;
import ru.hse.business.entity.ResponseData;
import ru.hse.learning_algorithm.TPMTrainer;
import ru.hse.tree_parity_machine.TreeParityMachine;

public class SynchronizationManager implements Handler {

    private static final Logger log = LoggerFactory.getLogger(SynchronizationManager.class);

    private TreeParityMachine tpm;
    private TPMTrainer trainer;
    private Controller controller;

    private boolean isSync;
    private int epochs;
    private int maxEpochs = 150;
    private short[] input;
    private int inputs;
    private short out;

    private byte curCommand;

    private static final byte NOP = 0;
    private static final byte INIT_W = 1;
    private static final byte INIT_X = 2;
    private static final byte TRAIN = 3;
    private static final byte SYNC_DONE = 4;

    private short out2;
    private short countSync;
    private static final short FLAG_SYNC_LIMIT = 40;

    // TODO: сделать автоопределение подключённых портов (Надо будет на GUI вызвать функцию определения всех портов и из списка их выбирать)
    public SynchronizationManager(TreeParityMachine tpm) {
        this.tpm = tpm;
        this.inputs = tpm.getTPMParams()[0];
        this.trainer = new TPMTrainer();
        this.controller = new ArduinoController(this, "/dev/ttyACM0", 115200);
    }

    @Override
    public void handleRequest(RequestData requestData) {
        switch (curCommand) {
            case INIT_W:
                if (!validateRequestData(requestData)) break;
                handleResponse(new ResponseData(INIT_X));
                epochs = 0;
                isSync = false;
                break;
            case INIT_X:
                if (!validateRequestData(requestData)) break;
                input = requestData.getVector();
                out = requestData.getOut();
                out2 = trainer.synchronize(tpm, input, out);
                handleResponse(new ResponseData(TRAIN, input, out2));
                epochs++;
                curCommand = TRAIN;
                break;
            case TRAIN:
                if (!validateRequestData(requestData)) break;
                input = requestData.getVector();
                out = requestData.getOut();
                countSync = out == tpm.getOutput(input) ? ++countSync : 0;
                if (countSync == FLAG_SYNC_LIMIT || epochs == maxEpochs) {
                    epochs = 0;
                    countSync = 0;
                    handleResponse(new ResponseData(SYNC_DONE));
                    curCommand = SYNC_DONE;
                    break;
                }
                out2 = trainer.synchronize(tpm, input, out);
                handleResponse(new ResponseData(TRAIN, input, out2));
                log.info("Current train epoch: {}", epochs);
                epochs++;
                break;
            case SYNC_DONE:
                if (!validateRequestData(requestData)) break;
                curCommand = NOP;
                log.info("Arduino weight: {}", requestData.getWeight());
                log.info("Computer weight: {}", tpm.getSecretKey());
                isSync = true;
                break;
        }
    }

    private boolean validateRequestData(RequestData requestData) {
        log.info("Data received: {}", requestData);
        if (!requestData.isOk()) {
            log.error("Bad response from Controller no Ok code");
            resendCommand();
            return false;
        }
        if (!requestData.vecHasLen(inputs)) {
            log.error("Bad response from Controller no len");
            resendCommand();
        }
        return true;
    }

    private void resendCommand() {
        if (curCommand == TRAIN)
            handleResponse(new ResponseData(curCommand, input, out2));
        else
            handleResponse(new ResponseData(curCommand));
    }

    @Override
    public void handleResponse(ResponseData responseData) {
        controller.sendMessage(responseData);
        curCommand = responseData.getCommand();
        log.info("Command was sent: {}, responseData: {}", curCommand, responseData);
    }

    public void generateKey() {
        handleResponse(new ResponseData(INIT_W));
    }

    public boolean isSync() {
        return isSync;
    }

    public byte getCurCommand() {
        return curCommand;
    }
}
