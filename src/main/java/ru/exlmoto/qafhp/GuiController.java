package ru.exlmoto.qafhp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
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
    private static TextArea textAreaReportStatic = null;

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
    private Button buttonSave = null;

    @FXML
    private Button buttonClear = null;

    @FXML
    private Button buttonAbout = null;

    @FXML
    private Button buttonTool = null;

    @FXML
    private Button buttonPost = null;

    @FXML
    private ProgressBar progressBar = null;

    @FXML
    private WebView webView = null;
    private WebEngine webEngine = null;

    @FXML
    private VBox rootWidget = null;

    private static QAndroidFileHostParser qAndroidFileHostParser = null;
    private PageWalker pageWalker = null;

    private boolean firstInsert = true;

    enum AppDialogs {
        Tool {
            @Override
            public String getLayoutPath() {
                return "/layouts/DialogTool.fxml";
            }
            @Override
            public void setAdditionalParameters(FXMLLoader loader, Stage stage) {
                ToolController toolController = loader.getController();
                toolController.setTextAreaFileOne(textAreaReportStatic.getText());
                toolController.setScene(stage.getScene());
                stage.setTitle("Texts and File Comparator Tool");
                stage.setMinHeight(640.0);
                stage.setMinWidth(800.0);
            }
        },
        Post {
            @Override
            public String getLayoutPath() {
                return "/layouts/DialogPost.fxml";
            }
            @Override
            public void setAdditionalParameters(FXMLLoader loader, Stage stage) {
                stage.setTitle("Send Post/Get Requests Tool");
            }
        },
        About {
            @Override
            public String getLayoutPath() {
                return "/layouts/DialogAbout.fxml";
            }
            @Override
            public void setAdditionalParameters(FXMLLoader loader, Stage stage) {
                AboutController aboutController = loader.getController();
                aboutController.setDialogStage(stage);
                aboutController.setqAndroidFileHostParser(qAndroidFileHostParser);
                stage.setTitle("About QAndroidFileHostParser");
                stage.setResizable(false);
            }
        };
        public abstract String getLayoutPath();
        public abstract void setAdditionalParameters(FXMLLoader loader, Stage stage);
    }

    @FXML
    private void initialize() {
        textFieldUrl.setText(PageTemplate.startUrl);
        spinnerPageStart.getEditor().textProperty().setValue(Integer.toString(PageTemplate.pageStart));
        spinnerPageStop.getEditor().textProperty().setValue(Integer.toString(PageTemplate.pageStop));
        spinnerPageItems.getEditor().textProperty().setValue(Integer.toString(PageTemplate.pageItems));
        spinnerPostDelay.getEditor().textProperty().setValue(Integer.toString(PageTemplate.postDelay));
        spinnerConTimeOut.getEditor().textProperty().setValue(Integer.toString(PageTemplate.conTimeout));
        textAreaReportStatic = textAreaReport;
        checkBoxMD5.setSelected(PageTemplate.settingMd5);
        textFieldUa.setText(PageTemplate.curlUa);
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
                FileWriter fileWriter = new FileWriter(file);
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
        showDialogHelper(AppDialogs.Tool);
    }

    @FXML
    private void aboutWork() {
        showDialogHelper(AppDialogs.About);
    }

    @FXML
    private void postWork() {
        showDialogHelper(AppDialogs.Post);
    }

    private void showDialogHelper(AppDialogs dialog) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(QAndroidFileHostParser.class.getResource(dialog.getLayoutPath()));
            VBox page = loader.load();

            Stage dialogStage = new Stage();
            Scene scene = new Scene(page);

            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(rootWidget.getScene().getWindow());
            dialogStage.setScene(scene);
            dialogStage.getIcons().add(new Image(QAndroidFileHostParser.class.getResourceAsStream("/icons/icon_sw.png")));

            dialog.setAdditionalParameters(loader, dialogStage);

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
        checkBoxMD5.setDisable(disable);
        buttonAbout.setDisable(disable);
        buttonClear.setDisable(disable);
        buttonTool.setDisable(disable);
        buttonSave.setDisable(disable);
        buttonPost.setDisable(disable);
    }

    public void setqAndroidFileHostParser(QAndroidFileHostParser qAndroidFileHostParser) {
        this.qAndroidFileHostParser = qAndroidFileHostParser;
    }
}
