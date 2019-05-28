package ru.hse.GUI.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StartWindowController {

    private final int WINDOW_WIDTH = 610;
    private final int WINDOW_HEIGHT = 575;

    private String COM_PORT = "";
    private ChatController chatController;

    public StartWindowController(List<String> listCOMPorts) {
        openStartWindow(getComPorts(listCOMPorts));
    }

    private void openStartWindow(ObservableList<String> listCOMPorts){
        Stage stage = new Stage();
        AnchorPane anchorPane = new AnchorPane();
        Scene scene = new Scene(anchorPane, WINDOW_WIDTH, WINDOW_HEIGHT);

        stage.setScene(scene);
        stage.setTitle("SecretChat");
        stage.getIcons().add(new Image("icon.jpeg"));

        scene.getStylesheets().add((getClass().getResource("/css/styles.css")).toExternalForm());


        Text titleWindow = new Text("Select COM port: ");
        titleWindow.setFont(Font.font("Arial",20));
        titleWindow.setFill(Color.GREY);
        titleWindow.setLayoutX(250);
        titleWindow.setLayoutY(60);

        Text textError = new Text("");
        textError.setFont(Font.font("Arial",18));
        textError.setFill(Color.DARKRED);
        textError.setLayoutX(110);
        textError.setLayoutY(90);


        TableView<String> table = new TableView<>();
        table.setLayoutX(80);
        table.setLayoutY(100);

        table.setMinHeight(WINDOW_HEIGHT - 200);
        table.setMaxHeight(WINDOW_HEIGHT - 200);
        table.setMaxWidth(WINDOW_WIDTH - 150);
        table.setMinWidth(WINDOW_WIDTH - 150);

        TableColumn<String, String> column = new TableColumn<>("COM port");
        column.setMaxWidth(WINDOW_WIDTH - 150);
        column.setMinWidth(WINDOW_WIDTH - 150);
        column.setResizable(false);

        table.getColumns().add(column);

        column.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue()));

        table.setItems(listCOMPorts);

        Button btnNext =  new Button();
        btnNext.setMaxWidth(100);
        btnNext.setMinWidth(100);
        btnNext.setMaxHeight(40);
        btnNext.setMinHeight(40);
        btnNext.setText("Next");
        btnNext.setFont(Font.font(18));
        btnNext.setLayoutX(270);
        btnNext.setLayoutY(500);

        //на кнопку далее
        btnNext.setOnAction((event) ->  {
            if (table.getSelectionModel().getSelectedItem() != null) {
                textError.setText("");
                COM_PORT = table.getSelectionModel().getSelectedItem();
                stage.close();
                // todo: COM_PORT преобразовать в имя пользователя
                try {
                    //todo: запрос на получения списка онлайн пользователей
                    List<Client> list = new ArrayList<>();
                    list.add(new Client("user1", new ArrayList<>()));
                    //
                    chatController = new ChatController();
                    chatController.openChatWindow(COM_PORT);
                    //chatController.setClientList(list);
                } catch (IOException e) { }
            }
            else {
                textError.setText("COM port is not selected!");
            }
        });

        anchorPane.getChildren().addAll(titleWindow, textError, table, btnNext);
        stage.show();
    }

    public String getCOM_PORT(){ return COM_PORT; }

    public ChatController getChatController() { return chatController; }

    private ObservableList<String> getComPorts(List<String> list){
        ObservableList<String> observableList = FXCollections.observableArrayList();
        observableList.addAll(list);
        return observableList;
    }

}
