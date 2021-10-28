package com.hehnev.client.netty.controller;

import com.hehnev.client.info.FileInfo;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class PanelController implements Initializable {
    @FXML
    public ComboBox<String> disksBox;
    @FXML
    public TextField pathField;
    @FXML
    private TableView<FileInfo> filesTable;

    private TableColumn<FileInfo, String> fileNameColumn, fileTypeColumn, fileDateColumn;
    private TableColumn<FileInfo, Long> fileSizeColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fileTypeColumnInit();
        fileNameColumnInit();
        fileSizeColumnInit();
        fileDateColumnInit();

        filesTable.getColumns().addAll(fileTypeColumn, fileNameColumn, fileSizeColumn, fileDateColumn);
        filesTable.getSortOrder().add(fileTypeColumn); // в качестве начальной сортировки столбец с типомы файлов

        showListDisks();

        updateList(Path.of(FileSystems.getDefault().getSeparator()));
    }

    private void fileDateColumnInit() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        fileDateColumn = new TableColumn<>("date modified");
        fileDateColumn.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().getLastModified().format(dtf)));
        fileDateColumn.setPrefWidth(120);
    }

    private void fileNameColumnInit() {
        fileNameColumn = new TableColumn<>("name");
        fileNameColumn.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().getFilename())); // return file name
        fileNameColumn.setPrefWidth(280); // Базовая длина столбца в пикселях
    }

    private void fileSizeColumnInit() {
        fileSizeColumn = new TableColumn<>("size");
        fileSizeColumn.setCellValueFactory(param ->
                new SimpleObjectProperty<>(param.getValue().getSize())); // return file size
        fileSizeColumn.setPrefWidth(200);

        fileSizeColumn.setCellFactory(column -> new TableCell<>() { // как выглядит ячейка в столбце
            @Override
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    String text = String.format("%,d bytes", item);
                    if (item == -1L) text = "[DIR]";
                    setText(text);
                }
            }
        });
    }

    private void fileTypeColumnInit() {
        fileTypeColumn = new TableColumn<>();
        fileTypeColumn.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().getType().getName())); // F and D
        fileTypeColumn.setPrefWidth(24); // Базовая длина столбца в пикселях
    }

    private void showListDisks() {
        disksBox.getItems().clear();
        for (Path path : FileSystems.getDefault().getRootDirectories()) {
            disksBox.getItems().add(path.toString());
        }
        disksBox.getSelectionModel().select(0);
    }

    public void updateList(Path path) {
        try {
            pathField.setText(path.normalize().toAbsolutePath().toString());
            filesTable.getItems().clear();
            filesTable.getItems().addAll(Files.list(path).map(FileInfo::new).collect(Collectors.toList()));
            filesTable.sort();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "failed to update the file list", ButtonType.OK);
            alert.showAndWait();
        }
    }

    public void btnPathUpAction(ActionEvent actionEvent) {
        Path upperPath = Path.of(pathField.getText()).getParent(); // ссылка на родителя
        if (upperPath != null) {
            updateList(upperPath);
        }
    }

    public void selectDiskAction(ActionEvent actionEvent) {
        ComboBox<String> element = (ComboBox<String>) actionEvent.getSource(); // получили ссылку на источник события
        updateList(Path.of(element.getSelectionModel().getSelectedItem())); // выбираем выделенный элемент С:\
    }

    public void mouseClicked(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Path path = Path.of(pathField.getText()).resolve(filesTable.getSelectionModel().getSelectedItem().getFilename());
            if (Files.isDirectory(path)) {
                updateList(path);
            }
        }
    }

    protected String getSelectedFilename() { // какой файл выбран в панеле
        if (!filesTable.isFocused()) return null;
        return filesTable.getSelectionModel().getSelectedItem().getFilename();
    }

    protected String getCurrentPath() {
        return pathField.getText();
    }
}
