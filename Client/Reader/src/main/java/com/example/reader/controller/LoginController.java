package com.example.reader.controller;

import com.example.reader.util.ShowError;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    ShowError errBox = new ShowError();

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;



    @FXML
    protected void onLoginClick() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {

            errBox.showError("Vui lòng nhập tên đăng nhập và mật khẩu.");
            return;
        }

        // TODO: Kết nối Server để đăng nhập - hiện tại đang để debug
        System.out.println("Login attempt: " + username);
        errBox.showError("Chức năng đăng nhập chưa kết nối Server.");
    }

    @FXML
    protected void onRegisterClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/reader/register-view.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) txtUsername.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
