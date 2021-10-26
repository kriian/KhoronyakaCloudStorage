package com.hehnev.client.netty;

import com.hehnev.client.info.FileInfo;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class Client implements Initializable {
    @FXML
    private TableView<FileInfo> fileTable;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        fileTable.getColumns().addAll(getFileTypeColumn(), getFileNameColumn());

        updateList(Path.of("root"));
    }

    public void btnExitAction(ActionEvent actionEvent) {
        Platform.exit();
    }

    private TableColumn<FileInfo, String> getFileNameColumn() {
        TableColumn<FileInfo, String> fileNameColumn = new TableColumn<>("name");
        fileNameColumn.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().getFilename())); // return file name
        fileNameColumn.setPrefWidth(240); // Базовая длина столбца в пикселях
        return fileNameColumn;
    }

    private TableColumn<FileInfo, String> getFileTypeColumn() {
        TableColumn<FileInfo, String> fileTypeColumn = new TableColumn<>();
        fileTypeColumn.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().getType().getName())); // F and D
        fileTypeColumn.setPrefWidth(24); // Базовая длина столбца в пикселях
        return fileTypeColumn;
    }

    private void updateList(Path path) {
        try {
            fileTable.getItems().clear();
            fileTable.getItems().addAll(Files.list(path).map(FileInfo::new).collect(Collectors.toList()));
            fileTable.sort();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "failed to update the file list", ButtonType.OK);
            alert.showAndWait();
        }
    }
}
