package ru.exlmoto.qafhp;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.scene.control.Alert.AlertType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ToolController {
    private Scene scene = null;
    private Alert alertError = null;

    @FXML
    private HBox container = null;

    @FXML
    private TextArea textAreaFileOne = null;

    @FXML
    private TextArea textAreaFileTwo = null;

    @FXML
    private TextArea textAreaUniq = null;

    @FXML
    private TextArea textAreaIden = null;

    @FXML
    private ProgressBar progressBarUniq = null;

    @FXML
    private Label labelStatus = null;

    @FXML
    private Label labelUniq = null;

    @FXML
    private CheckBox checkBoxMatchCase = null;

    @FXML
    private void initialize() {
        Platform.runLater(() -> textAreaFileOne.requestFocus());
        alertError = new Alert(AlertType.ERROR, "Files are Empty!", ButtonType.CLOSE);
    }

    @FXML
    private void openFileOne() {
        openFileHelper(textAreaFileOne);
    }

    @FXML
    private void openFileTwo() {
        openFileHelper(textAreaFileTwo);
    }

    @FXML
    private void workCSV() {

    }

    @FXML
    private void workNames() {

    }

    @FXML
    private void workStart() {
        if (checkTextFieldsFail()) {
            return;
        }
        textAreaIden.clear();
        textAreaUniq.clear();
        container.setDisable(true);
        try {
            Task<Void> task = createUniqTask(textAreaFileOne.getText(), textAreaFileTwo.getText(), checkBoxMatchCase.isSelected());
            progressBarUniq.progressProperty().unbind();
            progressBarUniq.progressProperty().bind(task.progressProperty());
            exec.submit(task);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void workSwap() {
        if (checkTextFieldsFail()) {
            return;
        }
        String oneText = textAreaFileOne.getText();
        textAreaFileOne.clear();
        textAreaFileOne.appendText(textAreaFileTwo.getText());
        textAreaFileTwo.clear();
        textAreaFileTwo.appendText(oneText);
    }

    private boolean checkTextFieldsFail() {
        if (textAreaFileOne.getText().trim().isEmpty()) {
            alertError.setContentText("Text Area #1 is Empty!");
            alertError.showAndWait();
            return true;
        }
        if (textAreaFileTwo.getText().trim().isEmpty()) {
            alertError.setContentText("Text Area #2 is Empty!");
            alertError.showAndWait();
            return true;
        }
        return false;
    }

    private void openFileHelper(TextArea textArea) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(scene.getWindow());
        if (file != null) {
            try {
                textArea.clear();
                textArea.appendText(new String(Files.readAllBytes(Paths.get(file.getPath()))));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private Task<Void> createUniqTask(String text1, String text2, boolean matchCase) {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                List<String> stringListOne = new ArrayList<>();
                List<String> stringListTwo = new ArrayList<>();
                Scanner scannerOne = new Scanner(text1);
                Scanner scannerTwo = new Scanner(text2);
                while (scannerOne.hasNextLine()) {
                    String s = scannerOne.nextLine().trim();
                    if (!s.isEmpty()) {
                        stringListOne.add(s);
                    }
                }
                updateProgress(5, 100);
                while (scannerTwo.hasNextLine()) {
                    String s = scannerTwo.nextLine().trim();
                    if (!s.isEmpty()) {
                        stringListTwo.add(s);
                    }
                }
                updateProgress(10, 100);
                scannerOne.close();
                scannerTwo.close();

                final int N = stringListOne.size();
                final int M = stringListTwo.size();
                int i_cnt = 0;
                int u_cnt = 0;
                int inner_cnt = 0;
                int inner_cnt2 = 0;
                float barPerc = 10.0f;
                float prop = (1.0f / (N / 100.0f)) * 0.9f;

                for (String aStringListOne : stringListOne) {
                    for (String aStringListTwo : stringListTwo) {
                        boolean bR;
                        if (matchCase) {
                            bR = aStringListOne.toUpperCase().equals(aStringListTwo.toUpperCase());
                        } else {
                            bR = aStringListOne.equals(aStringListTwo);
                        }
                        if (!bR) {
                            inner_cnt++;
                        }
                    }
                    if (inner_cnt == M) {
                        Platform.runLater(() -> textAreaUniq.appendText(aStringListOne + "\n"));
                        u_cnt++;
                    } else {
                        Platform.runLater(() -> textAreaIden.appendText(aStringListOne + "\n"));
                        i_cnt++;
                    }
                    inner_cnt2++;
                    if (inner_cnt2 >= prop) {
                        barPerc += prop;
                        updateProgress(Math.round(barPerc), 100);
                        inner_cnt2 = 0;
                    }
                    inner_cnt = 0;
                }
                updateProgress(100, 100);
                int finalU_cnt = u_cnt;
                int finalI_cnt = i_cnt;
                Platform.runLater(() -> labelUniq.setText(finalU_cnt + " unique strings. " + finalI_cnt + " identical strings."));
                Platform.runLater(() -> container.setDisable(false));
                return null;
            }
        };
    }

    private ExecutorService exec = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });

    public void setTextAreaFileOne(String text) {
        textAreaFileOne.setText(text);
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }
}
