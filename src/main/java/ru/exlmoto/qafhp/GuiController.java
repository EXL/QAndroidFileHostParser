package ru.exlmoto.qafhp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

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
    private WebView webView = null;
    private WebEngine webEngine = null;

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
        textFieldUa.setText(PageTemplate.curlUa);
        textFieldCookie.setText(PageTemplate.curlCookie);
        webEngine = webView.getEngine();
        pageWalker = new PageWalker(webView, webEngine, this);
        clearTextAreas();
    }

    @FXML
    private void startWork(ActionEvent event) {
        Button toggleButton = ((Button) event.getSource());
        boolean qToggleButtonState = toggleButton.getText().equals("Start!");
        disableAll(qToggleButtonState);
        if (qToggleButtonState) {
            PageTemplate.startUrl = textFieldUrl.getText();
            PageTemplate.pageItems = Integer.valueOf(spinnerPageItems.getEditor().getText());
            PageTemplate.pageStop = Integer.valueOf(spinnerPageStop.getEditor().getText());
            PageTemplate.pageStart = Integer.valueOf(spinnerPageStart.getEditor().getText());
            PageTemplate.postDelay = Integer.valueOf(spinnerPostDelay.getEditor().getText());
            PageTemplate.curlUa = textFieldUa.getText();
            PageTemplate.curlCookie = textFieldCookie.getText();

            toggleButton.setText("Stop!");

            textAreaReport.requestFocus();
            toLog("=== Parameters:");
            toLog("Page Start: " + PageTemplate.pageStart);
            toLog("Page Stop: " + PageTemplate.pageStop);
            toLog("Page Items: " + PageTemplate.pageItems);
            toLog("Post Delay: " + PageTemplate.postDelay * 100);
            toLog("=== Start working...");
            pageWalker.startWork();
        } else {
            toggleButton.setText("Start!");
            PageTemplate.pageCountAux = 0;
            firstInsert = true;
            webEngine.getLoadWorker().cancel();
            textFieldUrl.setText(PageTemplate.startUrl);
            clearTextAreas();
        }
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

    public void goToUrl(String url) {
        textFieldUrl.setText(url);
        webEngine.load(url);
    }

    private void clearTextAreas() {
        textAreaReport.setText("<empty>");
        textAreaLog.setText("Ready." + "\n");
    }

    public void toLog(String logText) {
        if (textAreaLog != null) {
            textAreaLog.appendText(logText + "\n");
        }
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

    private void disableAll(boolean disable) {
        if (checkBoxDisableWeb.isSelected()) {
            webView.setDisable(disable);
        }
        textFieldUrl.setDisable(disable);
        spinnerPageItems.setDisable(disable);
        spinnerPageStop.setDisable(disable);
        spinnerPageStart.setDisable(disable);
        spinnerPostDelay.setDisable(disable);
        spinnerConTimeOut.setDisable(disable);
        textFieldUa.setDisable(disable);
        textFieldCookie.setDisable(disable);
        checkBoxMD5.setDisable(disable);
    }
}
