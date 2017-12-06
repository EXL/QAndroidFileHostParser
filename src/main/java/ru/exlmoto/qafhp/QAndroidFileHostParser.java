package ru.exlmoto.qafhp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class QAndroidFileHostParser extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Create Window
        VBox form =  FXMLLoader.load(QAndroidFileHostParser.class.getResource("/layouts/QAndroidFileHostParser.fxml"));

        primaryStage.setTitle("QAndroidFileHostParser for BOXA by EXL, 2017");
        primaryStage.setScene(new Scene(form));
        primaryStage.setHeight(710.0);
        primaryStage.setWidth(1024.0);
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
