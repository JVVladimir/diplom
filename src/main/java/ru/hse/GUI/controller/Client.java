package ru.hse.GUI.controller;

import javafx.scene.layout.HBox;
import java.util.List;

public class Client {

    private String name;
    private List<HBox> history;

    public Client (String name, List<HBox> history) {
        this.name = name;
        this.history = history;
    }

    public String getName() {
        return name;
    }

    public List<HBox> getHistory() {
        return history;
    }

    public void setHistory(List<HBox> history) {
        this.history = history;
    }

    public void setName(String name) {
        this.name = name;
    }
}
