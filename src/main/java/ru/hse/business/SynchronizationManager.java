package ru.hse.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hse.arduino.ArduinoController;
import ru.hse.arduino.Controller;
import ru.hse.business.entity.RequestData;
import ru.hse.business.entity.ResponseData;
import ru.hse.learning_algorithm.TPMTrainer;
import ru.hse.tree_parity_machine.TreeParityMachine;

import java.util.Arrays;

public class SynchronizationManager implements Handler {

    private static final Logger log = LoggerFactory.getLogger(SynchronizationManager.class);

    private TreeParityMachine tpm;
    private TPMTrainer trainer;
    private Controller controller;

    private boolean isSync = false;
    private int epochs = 0;
    private int maxEpochs = 100;
    private short[] input;
    private int inputs;
    private short out;

    public static byte current_command;

    public static final byte INIT_W = 1;
    public static final byte INIT_X = 2;
    public static final byte TRAIN = 3;
    public static final byte SYNC_DONE = 4;
    public static final byte NOP = 0;
    public static final byte ENCRYPT = 5;
    public static final byte DECRYPT = ENCRYPT;

    private short out2;

    // TODO: сделать автоопределение подключённых портов
    public SynchronizationManager(TreeParityMachine tpm) {
        this.tpm = tpm;
        this.inputs = tpm.getTPMParams()[0];
        this.trainer = new TPMTrainer();
        this.controller = new ArduinoController(this, "/dev/ttyACM0", 115200);
    }

    @Override
    public void handleRequest(RequestData requestData) {
        switch (current_command) {
            case INIT_W:
                log.info("Data received: {}", requestData);
                if (!requestData.isOk()) {
                    log.error("Bad response from Controller while generating weights");
                    handleResponse(new ResponseData(INIT_W));
                    break;
                }
                handleResponse(new ResponseData(INIT_X));
                epochs = 0;
                isSync = false;
                break;
            case INIT_X:
                log.info("Data received: {}", requestData);
                if (!requestData.vecHasLen(inputs) || !requestData.isOk()) {
                    log.error("Bad response from Controller while generating Input");
                    handleResponse(new ResponseData(INIT_X));
                    break;
                }
                input = requestData.getVector();
                out = requestData.getOut();
                out2 = trainer.synchronize(tpm, input, out);
                log.info("Out2: {}", out2);
                ResponseData responseData = new ResponseData(TRAIN, input, out2);
                handleResponse(responseData);
                epochs++;
                current_command = TRAIN;
                break;
            case TRAIN:
                log.info("Data received: {}", requestData);
                ResponseData responseData1;
                if (!requestData.vecHasLen(inputs) || requestData.getMemory() == 0 || !requestData.isOk()) {
                    log.error("Bad response from Controller while training");
                    responseData1 = new ResponseData(TRAIN, input, out2);
                    handleResponse(responseData1);
                    break;
                }
                input = requestData.getVector();
                out = requestData.getOut();
                log.info("Memory: {}", requestData.getMemory());
                out2 = trainer.synchronize(tpm, input, out);
                log.info("Out1: {}, Out2: {}", out, out2);
                // TODO: возможно, сделать умную проверку, чтобы не считать до макс. числа итераций
                if (epochs == maxEpochs) {
                    epochs = 0;
                    handleResponse(new ResponseData(SYNC_DONE));
                    current_command = SYNC_DONE;
                    break;
                }
                responseData1 = new ResponseData(TRAIN, input, out2);
                handleResponse(responseData1);
                log.info("Current train epoch: {}", epochs);
                epochs++;
                break;
            case SYNC_DONE:
                log.info("Data received: {}", requestData);
                if (!requestData.isOk()) {
                    log.error("Bad response from Controller while accepting successful generating key");
                    handleResponse(new ResponseData(SYNC_DONE));
                    break;
                }
                current_command = NOP;
                System.out.println(Arrays.toString(requestData.getVector()) + "   " + requestData.getVector().length);
                System.out.println(Arrays.toString(tpm.getSecretKey())+ "   " + tpm.getSecretKey().length);
                isSync = true;
                break;
            case ENCRYPT:
                log.info("Data received: {}", requestData);
                break;
            default:
                log.info("Data received: {}", requestData);
        }
    }

    @Override
    public void handleResponse(ResponseData responseData) {
        controller.sendMessage(responseData);
        current_command = responseData.getCommand();
        log.info("Command was sended: {}, responseData: {}", current_command, responseData);
    }

    public void printStat() {
        /*((ArduinoController)controller).
        System.out.println(String.format("Промахов: %.2f%%, всего итераций: %.0f, 130 итераций за: %.2f сек.",
                ((countMiss / countAll) * 100), countAll, 130.0 / (countAll / 30)));*/
    }

    public boolean isSync() {
        return isSync;
    }

}
