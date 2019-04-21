package ru.hse.business;

import ru.hse.business.entity.RequestData;
import ru.hse.business.entity.ResponseData;

public interface Handler {

    void handleRequest(RequestData requestData);

    void handleResponse(ResponseData responseData);

}
