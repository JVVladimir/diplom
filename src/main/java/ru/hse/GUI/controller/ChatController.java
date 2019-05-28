package ru.hse.GUI.controller;

import com.jfoenix.controls.JFXDrawer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.AccessibleRole;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatController {

    public AnchorPane rootPane;
    public AnchorPane backgroundPane;
    public AnchorPane loadPane;
    public JFXDrawer listUsers;
     public AnchorPane usersPane;
    public ScrollPane usersScroll;
    public VBox usersBox;
    public AnchorPane chatPane;
    public TextArea msgText;
    public ScrollPane scrollMsg;
    public VBox chatBox;
    public Button btnSend;
    public Button btnFile;
    public Stage stage;

    private final int WINDOW_WIDTH = 610;
    private final int WINDOW_HEIGHT = 575;

    ObservableList<Client> clients;

    private String USERNAME = "user";

    private Client actualPerson;
    private List<HBox> actualHistory = new ArrayList<>();



    public void openChatWindow(String username) throws IOException {
        USERNAME = username;
        stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("chat.fxml"));
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        VBox vbox = new VBox(5);
        vbox.setPadding(new Insets(10));
        vbox.setAlignment(Pos.CENTER);

        ListView<Client> lvClients = new ListView<>();
        lvClients.setCellFactory(listView -> new ListCell<Client>() {
            @Override
            protected void updateItem(Client client, boolean empty) {
                super.updateItem(client, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hBox = new HBox(5);
                    hBox.setAlignment(Pos.CENTER);
                    hBox.getChildren().addAll(
                            new Circle(),
                            new Label(client.getName())
                    );
                    setGraphic(hBox);
                }
            }
        });
        lvClients.setItems(clients);

        stage.setScene(scene);
        stage.setTitle("SecretChat" + ": " + USERNAME);
        stage.getIcons().add(new Image("icon.jpeg"));
        stage.show();
    }

    /*
    //todo: если клиент лист поменялся -  испопльзуем метод getUser, чтоб получить текущий список пользователей и только потом удаляем оттуда пользователей которые вышли и добавляем новых пользователей, которые вошли
    //todo: затем используем этот метод - обязательно!!!
    public void updateListOnlineUsers(List<Client> clientList)  {
        this.actualPerson = null;
        this.actualHistory = new ArrayList<>();
        this.clientList = clientList;
        Platform.runLater(() -> usersBox.getChildren().clear());
        for(Client client : clientList) {
            if (client.getName().equals(USERNAME)) continue;
            if (this.actualPerson == null) this.actualPerson = client;
            HBox container = new HBox();
            container.setAlignment(Pos.CENTER_LEFT);
            container.setSpacing(10);
            container.setPrefWidth(usersBox.getPrefWidth());
            container.setPadding(new Insets(3));
            container.getStyleClass().add("online-user-container");
            Circle img = new Circle(30, 30, 15);
            container.getChildren().add(img);

            VBox userDetailContainer = new VBox();
            userDetailContainer.setPrefWidth(usersBox.getPrefWidth() / 1.7);

            Button btnUsername = new Button(client.getName());
            btnUsername.getStyleClass().add("online-label");
            btnUsername.setOnAction((ActionEvent actionEvent)->{
                this.actualPerson.setHistory(actualHistory);
                actualHistory = client.getHistory();
                actualPerson = client;
                chatBox.getChildren().clear();
                for (HBox hb: actualHistory) {
                    chatBox.getChildren().add(hb);
                }
            });
            userDetailContainer.getChildren().add(btnUsername);

            container.getChildren().add(userDetailContainer);

            Platform.runLater(() -> usersBox.getChildren().add(container));
        }
    }*/


    private void updateChat(String username,String message) {
        Text text=new Text(message);
        text.setFill(Color.WHITE);
        text.getStyleClass().add("message");

        TextFlow tempFlow=new TextFlow();

        if(!USERNAME.equals(username)){
            Text txtName=new Text(username + "\n");
            txtName.getStyleClass().add("txtName");
            tempFlow.getChildren().add(txtName);
        }

        tempFlow.getChildren().add(text);
        tempFlow.setMaxWidth(200);

        TextFlow flow=new TextFlow(tempFlow);

        HBox hbox=new HBox(12);

        if(!USERNAME.equals(username)){
            tempFlow.getStyleClass().add("tempFlowFlipped");
            flow.getStyleClass().add("textFlowFlipped");
            chatBox.setAlignment(Pos.TOP_LEFT);
            hbox.setAlignment(Pos.CENTER_LEFT);
            hbox.getChildren().add(flow);

        } else{
            text.setFill(Color.WHITE);
            tempFlow.getStyleClass().add("tempFlow");
            flow.getStyleClass().add("textFlow");
            hbox.setAlignment(Pos.BOTTOM_RIGHT);
            hbox.getChildren().add(flow);
        }

        actualHistory.add(hbox);
        hbox.getStyleClass().add("hbox");
        Platform.runLater(() -> chatBox.getChildren().addAll(hbox));
    }

    @FXML
    private void fileAction(ActionEvent actionEvent) {
        final FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            System.out.println(file.getAbsolutePath());
        }

    }

    @FXML
    private void sendAction(ActionEvent event) {
        if(msgText.getText().trim().equals(""))return;
        updateChat(USERNAME, msgText.getText());
        //todo ради теста
        if (actualPerson != null) acceptMessage(msgText.getText());
        msgText.setText("");
    }

    public void acceptMessage(String msg) {
        if(msg.equals(""))return;
        updateChat(this.actualPerson.getName(), msg);
    }

   public List<Client> getClientList(){ return new ArrayList<>(clients); }

    public void setClientList(List<Client> clientList) {
        this.clients = FXCollections.observableArrayList();
        this.clients.addAll(clientList);
    }

}
