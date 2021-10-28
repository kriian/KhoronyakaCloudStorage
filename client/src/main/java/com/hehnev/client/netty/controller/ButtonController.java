package com.hehnev.client.netty.controller;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class ButtonController {

    @FXML
    VBox leftPanel, rightPanel;

    public void btnExitAction(ActionEvent actionEvent) {
        Platform.exit();
    }


    public void copyBtnAction(ActionEvent actionEvent) {
        // обратились к свойствам контроллера
        PanelController leftPC = (PanelController) leftPanel.getProperties().get("ctrl");
        PanelController rightPC = (PanelController) rightPanel.getProperties().get("ctrl");

        if (leftPC.getSelectedFilename() == null && rightPC.getSelectedFilename() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "No file was selected", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        PanelController srcPC = null, dstPC = null;
        if (leftPC.getSelectedFilename() != null) {
            srcPC = leftPC;
            dstPC = rightPC;
        }
        if (rightPC.getSelectedFilename() != null) {
            srcPC = rightPC;
            dstPC = leftPC;
        }

        assert srcPC != null;
        Path srcPath = Path.of(srcPC.getCurrentPath(), srcPC.getSelectedFilename()); // какой файл копировать
        Path dstPath = Path.of(dstPC.getCurrentPath()).resolve(srcPC.getSelectedFilename()); // куда копируем

        try {
            Files.copy(srcPath, dstPath);
            dstPC.updateList(Path.of(dstPC.getCurrentPath()));
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "A file with this name already exist", ButtonType.OK);
            alert.showAndWait();
        }
    }
}
