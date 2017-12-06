package ru.exlmoto.qafhp;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AboutController {

    @FXML
    Button buttonClose = null;

    private QAndroidFileHostParser qAndroidFileHostParser = null;
    private Stage dialogStage = null;

    @FXML
    private void initialize() {
        Platform.runLater(() -> buttonClose.requestFocus());
    }

    @FXML
    private void close() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    @FXML
    private void openFc() {
        qAndroidFileHostParser.getHostServices().showDocument("https://firmware.center");
    }

    @FXML
    private void openEm() {
        qAndroidFileHostParser.getHostServices().showDocument("http://exlmoto.ru");
    }

    @FXML
    private void openGh() {
        qAndroidFileHostParser.getHostServices().showDocument("https://github.com/EXL/QAndroidFileHostParser");
    }

    public void setqAndroidFileHostParser(QAndroidFileHostParser qAndroidFileHostParser) {
        this.qAndroidFileHostParser = qAndroidFileHostParser;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
}
