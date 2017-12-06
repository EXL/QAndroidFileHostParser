package ru.exlmoto.qafhp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GuiController {
    @FXML
    private Button buttonStart = null;

    @FXML
    private TextField textFieldUrl = null;

    @FXML
    private TextArea textAreaLog = null;

    @FXML
    private TextArea textAreaReport = null;

    @FXML
    private Spinner spinnerPageStop = null;

    @FXML
    private Spinner spinnerPageItems = null;

    @FXML
    private Spinner spinnerPostDelay = null;

    @FXML
    private Spinner spinnerPageStart = null;

    @FXML
    private Spinner spinnerConTimeOut = null;

    @FXML
    private CheckBox checkBoxDisableWeb = null;

    @FXML
    private CheckBox checkBoxMD5 = null;

    @FXML
    private TextField textFieldUa = null;

    @FXML
    private TextField textFieldCookie = null;

    @FXML
    private Button buttonSave = null;

    @FXML
    private Button buttonClear = null;

    @FXML
    private Button buttonAbout = null;

    @FXML
    private Button buttonTool = null;

    @FXML
    private ProgressBar progressBar = null;

    @FXML
    private WebView webView = null;
    private WebEngine webEngine = null;

    @FXML
    private VBox rootWidget = null;

    private QAndroidFileHostParser qAndroidFileHostParser = null;
    private PageWalker pageWalker = null;

    private boolean firstInsert = true;

    @FXML
    private void initialize() {
        textFieldUrl.setText(PageTemplate.startUrl);
        spinnerPageStart.getEditor().textProperty().setValue(Integer.toString(PageTemplate.pageStart));
        spinnerPageStop.getEditor().textProperty().setValue(Integer.toString(PageTemplate.pageStop));
        spinnerPageItems.getEditor().textProperty().setValue(Integer.toString(PageTemplate.pageItems));
        spinnerPostDelay.getEditor().textProperty().setValue(Integer.toString(PageTemplate.postDelay));
        spinnerConTimeOut.getEditor().textProperty().setValue(Integer.toString(PageTemplate.conTimeout));

        checkBoxMD5.setSelected(PageTemplate.settingMd5);
        textFieldUa.setText(PageTemplate.curlUa);
        textFieldCookie.setText(PageTemplate.curlCookie);
        webEngine = webView.getEngine();
        pageWalker = new PageWalker(webView, webEngine, this);
        clearTextAreas();
    }

    @FXML
    private void startWork(ActionEvent event) {
        clearTextAreas();
        pageWalker.getFlashesArray().clear();
        PageTemplate.startUrl = textFieldUrl.getText();
        PageTemplate.pageItems = Integer.valueOf(spinnerPageItems.getEditor().getText());
        PageTemplate.pageStop = Integer.valueOf(spinnerPageStop.getEditor().getText());
        PageTemplate.pageStart = Integer.valueOf(spinnerPageStart.getEditor().getText());
        PageTemplate.postDelay = Integer.valueOf(spinnerPostDelay.getEditor().getText());
        PageTemplate.curlUa = textFieldUa.getText();
        PageTemplate.curlCookie = textFieldCookie.getText();
        PageTemplate.settingMd5 = checkBoxMD5.isSelected();

        disableAll(true);
        textAreaReport.requestFocus();
        PageTemplate.pageCountAux = 0;
        progressBar.progressProperty().bind(webEngine.getLoadWorker().progressProperty());

        toLog("=== Parameters:");
        toLog("Page Start: " + PageTemplate.pageStart);
        toLog("Page Stop: " + PageTemplate.pageStop);
        toLog("Page Items: " + PageTemplate.pageItems);
        toLog("Post Delay: " + PageTemplate.postDelay * 100);
        toLog("Con. Timeout: " + PageTemplate.conTimeout * 100);
        toLog("=== Start working...");

        goToUrl(PageTemplate.startUrl);
        pageWalker.startWork();
    }

    @FXML
    private void disableWebView(ActionEvent event) {
        CheckBox checkBox = (CheckBox) event.getSource();
        webView.setDisable(checkBox.isSelected());
        if (!checkBox.isSelected()) {
            toLog("Warning: WebView is On!");
        }
    }

    @FXML
    private void disableMD5(ActionEvent event) {
        PageTemplate.settingMd5 = ((CheckBox) event.getSource()).isSelected();
    }

    @FXML
    private void saveWork() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(rootWidget.getScene().getWindow());
        if (file != null) {
            try {
                FileWriter fileWriter = null;
                fileWriter = new FileWriter(file);
                fileWriter.write(textAreaReport.getText());
                fileWriter.close();
                toLog("Saving " + file.getName() + "... done!");
            } catch (IOException ex) {
                toLog(ex.toString());
            }
        }
    }

    @FXML
    private void clearWork() {
        clearTextAreas();
    }

    @FXML
    private void toolWork() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(QAndroidFileHostParser.class.getResource("/layouts/DialogTool.fxml"));
            VBox page = loader.load();

            Stage toolStage = new Stage();
            Scene scene = new Scene(page);

            ToolController toolController = loader.getController();

            toolStage.setTitle("Text File Comparator");
            toolStage.initModality(Modality.WINDOW_MODAL);
            toolStage.initOwner(rootWidget.getScene().getWindow());
            toolStage.setScene(scene);
            toolStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void aboutWork() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(QAndroidFileHostParser.class.getResource("/layouts/DialogAbout.fxml"));
            AnchorPane page = loader.load();

            Stage dialogStage = new Stage();
            Scene scene = new Scene(page);

            AboutController aboutController = loader.getController();
            aboutController.setDialogStage(dialogStage);
            aboutController.setqAndroidFileHostParser(qAndroidFileHostParser);

            dialogStage.setTitle("About QAFHP");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(rootWidget.getScene().getWindow());
            dialogStage.setScene(scene);
            dialogStage.setResizable(false);
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUrl(String url) {
        textFieldUrl.setText(url);
    }

    public void goToUrl(String url) {
        setUrl(url);
        webEngine.load(url);
    }

    private void clearTextAreas() {
        firstInsert = true;
        textAreaReport.setText("<empty>");
        textAreaLog.setText("Ready." + "\n");
    }

    public void toLog(String logText) {
        if (textAreaLog != null) {
            textAreaLog.appendText(logText + "\n");
        }
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void toReport(String line) {
        if (textAreaReport != null) {
            if (firstInsert) {
                textAreaReport.clear();
                firstInsert = false;
            }
            textAreaReport.appendText(line + "\n");
        }
    }

    public void disableAll(boolean disable) {
        if (checkBoxDisableWeb.isSelected()) {
            webView.setDisable(disable);
        }
        buttonStart.setDisable(disable);
        textFieldUrl.setDisable(disable);
        spinnerPageItems.setDisable(disable);
        spinnerPageStop.setDisable(disable);
        spinnerPageStart.setDisable(disable);
        spinnerPostDelay.setDisable(disable);
        spinnerConTimeOut.setDisable(disable);
        textFieldUa.setDisable(disable);
        textFieldCookie.setDisable(disable);
        checkBoxMD5.setDisable(disable);
        buttonAbout.setDisable(disable);
        buttonClear.setDisable(disable);
        buttonTool.setDisable(disable);
        buttonSave.setDisable(disable);
    }

    public void setqAndroidFileHostParser(QAndroidFileHostParser qAndroidFileHostParser) {
        this.qAndroidFileHostParser = qAndroidFileHostParser;
    }
}
