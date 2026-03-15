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

        // 1. Validate trống
        if (username.isEmpty() || password.isEmpty()) {
            MessageBox.showWarning("Vui lòng nhập tên đăng nhập và mật khẩu.");
            return;
        }

        try {
            // 2. Gọi DAO kiểm tra Database
            Account account = accountDAO.login(username, password, ROLE);
        // Gửi kèm role librarian tự động
        Account account = accountDAO.login(username, password, ROLE);

            if (account != null) {
                // 3. Đăng nhập thành công -> Mở màn hình chính
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/example/librarian/Library_Main_View/libra-main-view.fxml"));
                Scene scene = new Scene(loader.load());

                Stage stage = (Stage) txtUsername.getScene().getWindow();
                stage.setScene(scene);

                // Căn center cho cửa sổ và fullscreen
                stage.setTitle("Hệ thống Quản lý Thư viện CMCU");
                stage.setWidth(1200);
                stage.setHeight(700);
                stage.centerOnScreen();
                stage.setMaximized(true);
            } else {
                showAlert("Tài khoản hoặc mật khẩu không chính xác!");
            }

        } catch (RuntimeException e) {
            showAlert("Không kết nối được cơ sở dữ liệu!");
            e.printStackTrace();
        } catch (IOException e) {
            showAlert("Không mở được màn hình chính!");
            e.printStackTrace();
        }
    }

    // Hàm phụ trợ để hiển thị thông báo lỗi ngắn gọn
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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