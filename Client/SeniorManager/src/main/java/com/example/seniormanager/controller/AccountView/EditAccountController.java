package com.example.seniormanager.controller.AccountView;

import com.example.seniormanager.dao.AccountDAO;
import com.example.seniormanager.model.Account;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditAccountController {

    @FXML private TextField txtUsername;
    @FXML private TextField txtPassword;
    @FXML private Label lblStaffInfo;

    Account originalAccount;

    //Lấy dữ liệu account từ bảng đã chọn để sửa
    public void setAccountData(Account account){
        this.originalAccount = account;

        lblStaffInfo.setText("Nhân viên: " + account.getStaffName() + " ID: " + account.getStaffId());
        txtPassword.setText(account.getPasswordHash());
        txtUsername.setText(account.getUsername());
    }

    @FXML
    void handleUpdate(ActionEvent event) {
        String newUsername = txtUsername.getText().trim();
        String newPassword = txtPassword.getText().trim();

        if (newUsername.isEmpty() || newPassword.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Username và Password không được để trống!");
            return;
        }

        // So sánh thay đổi
        StringBuilder changes = new StringBuilder();
        if (!newUsername.equals(originalAccount.getUsername())) {
            changes.append("- Tên đăng nhập: ").append(originalAccount.getUsername()).append(" -> ").append(newUsername).append("\n");
        }
        if (!newPassword.equals(originalAccount.getPasswordHash())) {
            changes.append("- Mật khẩu: [Đã được thay đổi]\n");
        }

        if (changes.length() == 0) {
            showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Bạn chưa thay đổi dữ liệu nào cả.");
            return;
        }

        // Cập nhật CSDL
        boolean isSuccess = AccountDAO.updateAccount(originalAccount.getStaffId(), newUsername, newPassword);

        if (isSuccess) {
            showAlert(Alert.AlertType.INFORMATION, "Cập nhật thành công!", "Các mục đã thay đổi:\n" + changes.toString());
            closeWindow(event);
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể cập nhật tài khoản. Có thể Username đã tồn tại!");
        }
    }

    @FXML
    void handleCancel(ActionEvent event) {
        closeWindow(event);
    }

    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
