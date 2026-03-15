package com.example.librarian.controller;

import com.example.librarian.model.Account;
import com.example.librarian.util.MessageBox;
import com.example.librarian.dao.AccountDAO;
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

    private static String ROLE = "Librarian";

    @FXML
    protected void onLoginClick() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            MessageBox.showWarning("Vui lòng nhập tên đăng nhập và mật khẩu.");
            return;
        }

        // Gửi kèm role librarian tự động
        Account account = accountDAO.login(username, password, ROLE);

        if (account != null) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/example/librarian/Library_Main_View/libra-main-view.fxml"));
                Scene scene = new Scene(loader.load());
                Stage stage = (Stage) txtUsername.getScene().getWindow();
                stage.setScene(scene);
            } catch (IOException e) {
                MessageBox.showError("Không thể mở trang chính.", e.getMessage() != null
                        ? e.getMessage() : "Lỗi không xác định");
            }
        } else {
            MessageBox.showError("Sai tên đăng nhập mật khẩu.");
        }
    }
}
