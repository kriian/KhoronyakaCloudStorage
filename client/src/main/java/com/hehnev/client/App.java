package com.hehnev.client;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("main.fxml")));
        stage.setScene(new Scene(parent, 1280, 600));
        stage.setTitle("Khoronyaka Cloud Storage");
        stage.show();
    }
}