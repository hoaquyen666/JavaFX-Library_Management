package com.example.reader.controller;

import com.example.reader.util.ShowError;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterController {

    ShowError errBox = new ShowError();

    @FXML
    private TextField txtUsername;

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private PasswordField txtConfirmPassword;

    @FXML
    private Label lblError;

    @FXML
    protected void onRegisterClick() {
        String username = txtUsername.getText().trim();
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText().trim();
        String confirmPassword = txtConfirmPassword.getText().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            errBox.showError("Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            errBox.showError("Mật khẩu xác nhận không khớp.");
            return;
        }

        // TODO: Kết nối Server để đăng ký tài khoản - hiện tại vẫn để debug
        System.out.println("Register attempt: " + username + " / " + email);
        errBox.showError("Chức năng đăng ký chưa kết nối Server.");
    }

    @FXML
    protected void onLoginClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/reader/login-view.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) txtUsername.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
