package ru.hse.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hse.arduino.ArduinoController;
import ru.hse.arduino.Controller;
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
    private int maxEpochs = 130;
    private byte[] input;
    private int inputs;
    private int out;

    private static byte current_command;

    public static final byte INIT_W = 1;
    public static final byte INIT_X = 2;
    public static final byte TRAIN = 3;
    public static final byte SYNC_DONE = 4;
    public static final byte NOP = 0;
    public static final byte ENCRYPT = 5;
    public static final byte DECRYPT = ENCRYPT;

    // private int[] ints;
    private byte[] result;
    private int out2;

    public SynchronizationManager(TreeParityMachine tpm) {
        this.tpm = tpm;
        this.inputs = tpm.getTPMParams()[0];
        this.trainer = new TPMTrainer();
        this.controller = new ArduinoController(this, "COM3", 250000, 8, 1, 0);
        controller.openPort();
    }

    @Override
    public void handleRequest(byte[] data) {
        /*if(data.length == 2 && data[0] == 13 && data[1] == 10) {
            return;
        }*/
        int[] ints;
        switch (current_command) {
            case INIT_W:
                log.info("Code send: {}, data received: {}", current_command, data);
                if (data.length == 0 || data[0] != 100) {
                    log.error("Bad response from Controller while generating weights");
                    handleResponse(new byte[]{INIT_W});
                    break;
                }
                handleResponse(new byte[]{INIT_X});
                epochs = 0;
                isSync = false;
                break;
            case INIT_X:
                log.info("Code send: {}, data received: {}", current_command, data);
                if (data.length == 0 || data.length != inputs + 2 || data[0] != 100) {
                    log.error("Bad response from Controller while generating Input");
                    handleResponse(new byte[]{INIT_X});
                    break;
                }
                input = Arrays.copyOfRange(data, 1, data.length - 1);
                out = data[data.length - 1];
                // TODO: этого говна тут не будет!!!!
                ints = new int[input.length];
                for (int i = 0; i < ints.length; i++)
                    ints[i] = input[i];
                out2 = trainer.synchronize(tpm, ints, out);
                log.info("Out2: {}", out2);
                result = new byte[input.length + 2];
                result[0] = TRAIN;
                System.arraycopy(input, 0, result, 1, input.length);
                result[result.length - 1] = (byte) out2;
                handleResponse(result);
                epochs++;
                current_command = TRAIN;
                break;
            case TRAIN:
                log.info("Code send: {}, data received: {}", current_command, data);
                if (data.length == 0 || data.length != inputs + 3 || data[0] != 100) {
                    log.error("Bad response from Controller while training");
                    result = new byte[input.length + 2];
                    result[0] = TRAIN;
                    System.arraycopy(input, 0, result, 1, input.length);
                    result[result.length - 1] = (byte) out2;
                    handleResponse(result);
                    // handleResponse(new byte[]{TRAIN});
                    break;
                }
                input = Arrays.copyOfRange(data, 1, data.length - 2);
                out = data[data.length - 2];
                log.info("Memory: {}", data[data.length-1]*5);
                // TODO: этого говна тут не будет!!!!
                ints = new int[input.length];
                for (int i = 0; i < ints.length; i++)
                    ints[i] = input[i];
                out2 = trainer.synchronize(tpm, ints, out);
                log.info("Out2: {}", out2);
                // TODO: возможно, сделать умную проверку, чтобы не считать до макс. числа итераций
                if (epochs == maxEpochs) {
                    epochs = 0;
                    handleResponse(new byte[]{SYNC_DONE});
                    current_command = SYNC_DONE;
                    System.out.println(String.format("*****  %s  ********", Arrays.toString(tpm.getSecretKey())));
                    break;
                }
                result = new byte[input.length + 2];
                result[0] = TRAIN;
                System.arraycopy(input, 0, result, 1, input.length);
                result[result.length - 1] = (byte) out2;
                handleResponse(result);
                log.info("Current train epoch: {}", epochs);
                epochs++;
                break;
            case SYNC_DONE:
                log.info("Code send: {}, data received: {}", current_command, data);
                if (data.length == 0 || data[0] != 100) {
                    log.error("Bad response from Controller while accepting successful generating key");
                    handleResponse(new byte[]{SYNC_DONE});
                    break;
                }
                current_command = NOP;
                isSync = true;
                break;
            case ENCRYPT:
                log.info("Data received: {}", data);
                break;
                default:
                    log.info("Data received: {}", data);
        }
    }

    @Override
    public void handleResponse(byte[] data) {
        controller.sendMessage(data);
        current_command = data[0];
        log.info("Command was sended: {}, data: {}", current_command, data);
    }

    public boolean isSync() {
        return isSync;
    }

}