package com.example.librarian.controller;

import com.example.librarian.dao.AccountDAO;
import com.example.librarian.model.Account;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    private AccountDAO accountDAO = new AccountDAO();

    private static String ROLE = "Librarian";

    @FXML
    protected void onLoginClick() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Vui lòng nhập đầy đủ Tài khoản và Mật khẩu!");
            return;
        }

        Account account = accountDAO.login(username, password, ROLE);

        if (account != null) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/example/librarian/Library_Main_View/libra-main-view.fxml"));
                Scene scene = new Scene(loader.load());
                // scene.getStylesheets().add(getClass().getResource("/com/example/librarian/Library_Main_Viewe/libra-main.css").toExternalForm());
                Stage stage = (Stage) txtUsername.getScene().getWindow();
                stage.setScene(scene);
                // căn center cho cửa sổ và fullscreen
                stage.setTitle("Hệ thống Quản lý Thư viện CMCU");
                stage.setWidth(1200);
                stage.setHeight(700);
                stage.centerOnScreen();
                stage.setMaximized(true);
            } catch (IOException e) {
                       e.printStackTrace();
            }
        } else {
            showAlert("Tài khoản hoặc mật khẩu không chính xác!");
        }
    }
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi đăng nhập");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
