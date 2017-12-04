package ru.exlmoto.qafhp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class QAndroidFileHostParser extends Application {

    private WebView webView = null;
    private WebEngine webEngine = null;
    private PageWalker pageWalker = null;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Init WebBrowser
        webView = new WebView();
        webEngine = webView.getEngine();

        //
        pageWalker = new PageWalker(webView, webEngine);
        pageWalker.startWalk();

        // Create Window
        VBox window = (VBox) FXMLLoader.load(
                QAndroidFileHostParser.class.getResource("/layouts/QAndroidFileHostParser.fxml"));

        primaryStage.setTitle("QAndroidFileHostParser for BOXA");
        primaryStage.setScene(new Scene(window));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
