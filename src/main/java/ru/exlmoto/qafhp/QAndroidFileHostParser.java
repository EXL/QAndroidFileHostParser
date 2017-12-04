package ru.exlmoto.qafhp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class QAndroidFileHostParser extends Application {

    @Override
    public void start(Stage primaryStage) {
        StackPane root = new StackPane();
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
