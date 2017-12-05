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
    private CheckBox checkBoxDisableWeb = null;

    @FXML
    private WebView webView = null;
    private WebEngine webEngine = null;

    private PageWalker pageWalker = null;

    @FXML
    private void initialize() {
        textFieldUrl.setText(PageTemplate.startUrl);
        textAreaReport.setText("<empty>");
        spinnerPageStop.getEditor().textProperty().setValue(Integer.toString(PageTemplate.pageStop));
        spinnerPageItems.getEditor().textProperty().setValue(Integer.toString(PageTemplate.pageItems));
        webEngine = webView.getEngine();
        pageWalker = new PageWalker(webView, webEngine, this);
        toLog("Ready.");
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

            toggleButton.setText("Stop!");
            textAreaReport.requestFocus();
            toLog("Start working...");
            toLog("Page Stop: " + spinnerPageStop.getEditor().getText());
            toLog("Page Items: " + spinnerPageItems.getEditor().getText());
            pageWalker.startWork();
        } else {
            toggleButton.setText("Start!");
            webEngine.getLoadWorker().cancel();
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

    public void toLog(String logText) {
        if (textAreaLog != null) {
            textAreaLog.appendText(logText + "\n");
        }
    }

    public void toReport(String line) {
        if (textAreaReport != null) {
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
    }
}
