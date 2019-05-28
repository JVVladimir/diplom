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
import ru.hse.GUI.ClientGUILead;
import ru.hse.net.Message;
import ru.hse.net.NetManagerLead;
import ru.hse.utils.Encrypter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

    public static ObservableList<Client> clients;

    private String USERNAME = "user";

    private Client actualPerson;
    private List<HBox> actualHistory = new ArrayList<>();
    private static ClientGUILead clientGUILead;

    @FXML
    void initialize() {
        updateListOnlineUsers(clients);
        if (clients.size()>1) clientGUILead.generateKey();
    }

    public ChatController() {}

    public ChatController(List<Client> cl)  {
        clients = FXCollections.observableArrayList();
        clients.addAll(cl);
    }

    public void openChatWindow(String comport, ClientGUILead clientGUI) throws IOException {
        clientGUILead = clientGUI;
        USERNAME = System.getProperty("user.name");
        stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("chat.fxml"));
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.setTitle("SecretChat" + ": " + USERNAME);
        stage.getIcons().add(new Image("icon.jpeg"));
        stage.show();
    }


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


    public void updateListOnlineUsers(List<Client> cl)  {
        this.actualPerson = null;
        this.actualHistory = new ArrayList<>();
        Platform.runLater(() -> usersBox.getChildren().clear());
        for(Client client : cl) {
            if (client.getName().equals(System.getProperty("user.name"))) continue;
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
        if (clientGUILead.isReady) {
            clientGUILead.netManagerLead.connection.sendMessage(
                    new Message(NetManagerLead.SEND, System.getProperty("user.name"),
                            Encrypter.encrypt(msgText.getText().getBytes(), clientGUILead.netManagerLead.key)));
            updateChat(USERNAME, msgText.getText());
            msgText.setText("");
        }
    }


    public void acceptMessage(String msg) {
        if(msg.equals(""))return;
        updateChat(this.actualPerson.getName(), msg);
    }

}
