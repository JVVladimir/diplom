package ru.hse.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hse.business.entity.RequestData;
import ru.hse.business.entity.ResponseData;

public class SlaveSynchronizationManager extends SynchronizationManager implements Handler {

    private static final Logger log = LoggerFactory.getLogger(SlaveSynchronizationManager.class);

    private static final int INIT_W_SLAVE = 10;

    public SlaveSynchronizationManager() {
        super(INIT_W_SLAVE);
    }

    @Override
    public void handleRequest(RequestData requestData) {
        if (!validateRequestData(requestData)) {
            resendCurrentCommand();
            return;
        }
        this.requestData = requestData;
        wakeUp();
        switch (curCommand) {
            case INIT_W_SLAVE:
                epochs = 0;
                break;
            case TRAIN:
                epochs++;
                break;
            case SYNC_DONE:
                curCommand = NOP;
                epochs = 0;
                break;
        }
    }

    private synchronized void wakeUp() {
        taskDone = true;
        notifyAll();
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
      //  log.info("Command was sent: {}, responseData: {}", curCommand, responseData);
    }

    // TODO: в будущем добавить функцию, которая будет задавать параметры ДМЧ в Ардуино
    // requestData - проверка, что не null
    @Override
    public RequestData initWeights() {
        handleResponse(new ResponseData(INIT_W_SLAVE));
        waitTask();
        return requestData;
    }

    @Override
    public RequestData initInput() {
        throw new RuntimeException("Not supported operation!");
    }

    // requestData - вынуть из неё out
    @Override
    public RequestData train() {
        handleResponse(new ResponseData(TRAIN, input, out2));
        waitTask();
        return requestData;
    }

    // requestData - вынуть из неё ключ
    @Override
    public RequestData syncDone() {
        handleResponse(new ResponseData(SYNC_DONE));
        waitTask();
        log.info("Key got: {}", requestData.getWeight());
        return requestData;
    }

}