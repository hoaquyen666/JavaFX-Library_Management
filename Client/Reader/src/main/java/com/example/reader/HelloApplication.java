package com.example.reader;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1080, 630);
        scene.getStylesheets().add(HelloApplication.class.getResource("reader.css").toExternalForm());
        stage.setTitle("Reader Management");
        stage.setMinWidth(980);
        stage.setMinHeight(560);
        stage.setScene(scene);
        stage.show();
    }
}
