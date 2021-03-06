package ru.hse.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hse.business.entity.RequestData;
import ru.hse.business.entity.ResponseData;

public class SlaveSynchronizationManager extends SynchronizationManager implements Handler {

    private static final Logger log = LoggerFactory.getLogger(SlaveSynchronizationManager.class);

    private static final int INIT_W_SLAVE = 10;

    public SlaveSynchronizationManager(String COMPort) {
        super(INIT_W_SLAVE, COMPort);
    }

    @Override
    public void handleRequest(RequestData requestData) {
        if (!validateRequestData(requestData)) {
            resendCurrentCommand();
            return;
        }
        this.requestData = requestData;
        switch (curCommand) {
            case INIT_W_SLAVE:
                taskDone = true;
                epochs = 0;
                break;
            case TRAIN:
                taskDone = true;
                log.info("Current train epoch: {}", epochs);
                log.info("Current out: {}", requestData.getOut());
                epochs++;
                break;
            case SYNC_DONE:
                taskDone = true;
                curCommand = NOP;
                log.info("Epochs trained: {}", epochs);
                log.info("Arduino weight: {}", requestData.getWeight());
                epochs = 0;
                break;
        }
    }

    private void resendCurrentCommand() {
        if (curCommand == TRAIN)
            handleResponse(new ResponseData(curCommand, requestData.getIn(), requestData.getOut()));
        else
            handleResponse(new ResponseData(curCommand));
    }

    @Override
    public void handleResponse(ResponseData responseData) {
        controller.sendMessage(responseData);
        curCommand = responseData.getCommand();
        log.info("Command was sent: {}, responseData: {}", curCommand, responseData);
    }

    // TODO: в будущем добавить функцию, которая будет задавать параметры ДМЧ в Ардуино
    @Override
    public RequestData initWeights() {
        handleResponse(new ResponseData(INIT_W_SLAVE));
        waitTask();
        log.info("Weights generated");
        return requestData;
    }

    @Override
    public RequestData initInput() {
        throw new RuntimeException("Not supported operation!");
    }

    @Override
    public RequestData train() {
        handleResponse(new ResponseData(TRAIN, input, out2));
        waitTask();
        log.info("Выход получен out: {} абонент 2", requestData.getOut());
        return requestData;
    }

    @Override
    public RequestData syncDone() {
        handleResponse(new ResponseData(SYNC_DONE));
        waitTask();
        log.info("Key got: {}", requestData.getWeight());
        return requestData;
    }

}