package ru.hse.business;

public interface Handler {

    void handleRequest(byte[] data);

    void handleResponse(byte[] command);

}
