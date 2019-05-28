package ru.hse.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hse.business.entity.RequestData;
import ru.hse.business.entity.ResponseData;

public class LeadSynchronizationManager extends SynchronizationManager {

    private static final Logger log = LoggerFactory.getLogger(LeadSynchronizationManager.class);

    private static final int INIT_W_LEAD = 1;

    public LeadSynchronizationManager() {
        super(INIT_W_LEAD);
    }

    @Override
    public void handleRequest(RequestData requestData) {
        if (!validateRequestData(requestData)) {
            resendCurrentCommand();
            return;
        }
        this.requestData = requestData;
        switch (curCommand) {
            case INIT_W_LEAD:
                taskDone = true;
                epochs = 0;
                isSync = false;
                break;
            case INIT_X:
                input = requestData.getInput();
                taskDone = true;
                break;
            case TRAIN:
                input = requestData.getInput();
                taskDone = true;
                epochs++;
                break;
            case SYNC_DONE:
                taskDone = true;
                curCommand = NOP;
                epochs = 0;
                isSync = true;
                break;
        }
    }

    protected void resendCurrentCommand() {
        if (curCommand == TRAIN)
            handleResponse(new ResponseData(curCommand, input, out2));
        else
            handleResponse(new ResponseData(curCommand));
    }

    @Override
    public void handleResponse(ResponseData responseData) {
        controller.sendMessage(responseData);
        curCommand = responseData.getCommand();
    }

    // TODO: в будущем добавить функцию, которая будет задавать параметры ДМЧ в Ардуино
    // requestData - проверка, что не null
    @Override
    public RequestData initWeights() {
        handleResponse(new ResponseData(INIT_W_LEAD));
        waitTask();
        log.info("Weights generated");
        return requestData;
    }

    // requestData - вынуть вход и выход
    @Override
    public RequestData initInput() {
        handleResponse(new ResponseData(INIT_X));
        waitTask();
        log.info("Input generated: {}, out: {}", requestData.getInput(), requestData.getOut());
        return requestData;
    }

    // requestData - вынуть из неё out
    @Override
    public RequestData train(RequestData data) {
        handleResponse(new ResponseData(TRAIN, input, out2));
        waitTask();
     //   log.info("Input: {}, out: {}", requestData.getInput(), requestData.getOut());
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
