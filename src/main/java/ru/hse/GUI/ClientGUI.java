package ru.hse.GUI;


import javafx.application.Application;
import javafx.stage.Stage;
import ru.hse.GUI.controller.ChatController;
import ru.hse.GUI.controller.StartWindowController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ClientGUI extends Application {


    public static void main(String[] args) throws IOException {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        List<String> listCOMPorts = new ArrayList<>();
        listCOMPorts.add("COM11111");
        listCOMPorts.add("COM121111");
        listCOMPorts.add("COM1333331111");
        listCOMPorts.add("COM6654111144351");
        StartWindowController startWindowController = new StartWindowController(listCOMPorts);
    }
}
