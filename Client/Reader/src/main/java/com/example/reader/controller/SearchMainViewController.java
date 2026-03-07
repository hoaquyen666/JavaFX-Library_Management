package com.example.reader.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;

public class SearchMainViewController {

    @FXML
    private Button btnLogin;

    @FXML
    private Button btnRegister;

    @FXML
    protected void onLoginForm() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/reader/login-view.fxml"));
        Scene scene = new Scene(loader.load(), 400, 370);
        Stage stage = (Stage) btnLogin.getScene().getWindow();
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    @FXML
    protected void onRegisterForm() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/reader/register-view.fxml"));
        Scene scene = new Scene(loader.load(), 400, 500);
        Stage stage = (Stage) btnRegister.getScene().getWindow();
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
}
