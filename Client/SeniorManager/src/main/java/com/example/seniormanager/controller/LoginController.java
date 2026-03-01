package com.example.seniormanager.controller;

import com.example.seniormanager.dao.AccountDAO;
import com.example.seniormanager.model.Account;
import com.example.seniormanager.util.MessageBox;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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

    private static String ROLE = "Senior";

    @FXML
    protected void onLoginClick() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            MessageBox.showWarning("Vui lòng nhập tên đăng nhập và mật khẩu.");
            return;
        }

        // Gửi kèm role "Senior" tự động
        Account account = accountDAO.login(username, password, ROLE);

        if (account != null) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/example/seniormanager/main-view.fxml"));
                Scene scene = new Scene(loader.load());
                scene.getStylesheets().add(getClass().getResource("/style/ButtonStyle.css").toExternalForm());
                Stage stage = (Stage) txtUsername.getScene().getWindow();
                stage.setScene(scene);
            } catch (IOException e) {
                MessageBox.showError("Không thể mở trang chính.", e.getMessage() != null
                        ? e.getMessage() : "Lỗi không xác định");
            }
        } else {
            MessageBox.showError("Sai tên đăng nhập hoặc mật khẩu.");
        }
    }

    @FXML
    protected void onTicketHelpClick() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/seniormanager/tickethelp-view.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) txtUsername.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            MessageBox.showError(e.getMessage());
        }
    }
}
