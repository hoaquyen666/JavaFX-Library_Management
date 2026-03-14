package com.example.seniormanager.controller.AccountView;

import com.example.seniormanager.dao.AccountDAO;
import com.example.seniormanager.model.StaffOption;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AddAccountController implements Initializable {
    @FXML private ComboBox cbStaff;
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> cbRole;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbRole.getItems().addAll("Librarian" ,"Senior");
        cbRole.getSelectionModel().selectFirst();

        List<StaffOption> staffList = AccountDAO.getStaffsWithoutAccount();
        cbStaff.getItems().addAll(staffList);
    }

    @FXML
    void handleSave(ActionEvent event){
        StaffOption selectedStaff = (StaffOption) cbStaff.getValue();
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();
        String role = cbRole.getValue();

        if (selectedStaff == null || username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Lỗi nhập liệu", "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        int staffId = selectedStaff.getStaffId();
        boolean isSuccess = AccountDAO.insertStaffAccount(staffId, username, password, role);

        if (isSuccess) {
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã thêm tài khoản mới thành công!");
            closeWindow(event);
        } else {
            showAlert(Alert.AlertType.ERROR, "Thất bại", "Không thể thêm tài khoản. Có thể Username đã tồn tại!");
        }
    }

    @FXML
    void handleCancel(ActionEvent event){
        closeWindow(event);
    }
    // Hàm hỗ trợ đóng cửa sổ hiện tại
    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
