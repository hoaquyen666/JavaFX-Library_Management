package com.example.reader;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/reader/login-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1500, 1000);

//        scene.getStylesheets().add(
//                HelloApplication.class.getResource("/com/example/reader/search-view/search-style.css").toExternalForm()
//        );

        stage.setTitle("Thu Vien - Reader");
        stage.setScene(scene);
        stage.show();

        }}
