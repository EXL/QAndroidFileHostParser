package ru.exlmoto.qafhp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
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
        StackPane rootWidget = new StackPane();
        rootWidget.getChildren().add(webView);

        primaryStage.setTitle("QAndroidFileHostParser for BOXA");
        primaryStage.setScene(new Scene(rootWidget, 800, 600));
        primaryStage.show();

        pageWalker.sendPost();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
