package com.hehnev.client.io;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements Initializable {

    @FXML
    public ListView<String> listView;
    @FXML
    public TextField input;

    private Client client;
    private DataInputStream dis;
    private DataOutputStream dos;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            client = new Client();
            client.showFilesName(listView, input);
            dis = client.getDis();
            dos = client.getDos();
            Thread readThread = new Thread(() -> {
                try {
                    while (true) {
                        String msg = dis.readUTF();
                        Platform.runLater(() -> input.setText(msg));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            readThread.setDaemon(true);
            readThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendFileToServer(ActionEvent actionEvent) {
        try {
            client.sendBytesStream(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}