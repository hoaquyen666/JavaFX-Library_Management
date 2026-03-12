package com.example.seniormanager.controller.StaffView;

import com.example.seniormanager.dao.StaffInfoDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddStaffInfoController implements Initializable {
    @FXML private TextField txtFullName;
    @FXML private TextField txtEmail;
    @FXML private DatePicker dpDob;
    @FXML private TextField txtPhone;
    @FXML private ComboBox cbRole;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbRole.getItems().addAll("Librarian");
        cbRole.getSelectionModel().selectFirst();
    }

    @FXML
    void handleSave(ActionEvent event){
        String name = txtFullName.getText().trim();
        String email = txtEmail.getText().trim();
        LocalDate dob = dpDob.getValue();
        String phone = txtPhone.getText().trim();
        String role = cbRole.getValue().toString();

        if(name.isEmpty()|| email.isEmpty() || dob == null || phone.isEmpty() ){
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Cần nhập đầy đủ");
            return;
        }

        boolean isSuccess = StaffInfoDAO.insertStaff(name, email, dob, phone, role);

        if(isSuccess){
            showAlert(Alert.AlertType.INFORMATION, "Thêm nhân viên", "Thành công thêm nhân viên");
        }else showAlert(Alert.AlertType.ERROR, "Không thêm được nhân viên", "Không nhân viên nào được thêm");

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
