package ru.hse.business;

public interface Handler {

    void handleRequest(String data);

    void handleResponse(String command);

}
