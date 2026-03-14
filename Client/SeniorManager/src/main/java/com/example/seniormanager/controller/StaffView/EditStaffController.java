package com.example.seniormanager.controller.StaffView;

import com.example.seniormanager.dao.StaffInfoDAO;
import com.example.seniormanager.model.StaffInfo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class EditStaffController implements Initializable{
    @FXML private Label lblStaffCode;
    @FXML private TextField txtFullName;
    @FXML private TextField txtEmail;
    @FXML private DatePicker dpDob;
    @FXML private TextField txtPhone;

    private StaffInfo originalStaff;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    //Set data từ dòng chọn lên
    public void setStaffData(StaffInfo staff){
        this.originalStaff = staff;

        lblStaffCode.setText("(Mã NV: " + staff.getStaffCode() + ")");
        txtFullName.setText(staff.getFullName());
        txtEmail.setText(staff.getEmail());
        dpDob.setValue(staff.getDoB());
        txtPhone.setText(staff.getPhoneNumber());
    }

    @FXML
    void handleUpdate(ActionEvent event){
        String newName = txtFullName.getText().trim();
        String newEmail = txtEmail.getText().trim();
        String newPhone = txtPhone.getText().trim();
        LocalDate newDob = dpDob.getValue();

        if (newName.isEmpty() || newEmail.isEmpty() || newDob == null || newPhone.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Không được để trống dữ liệu!");
            return;
        }

        // So sánh tìm ra sự thay đổi để làm báo cáo
        StringBuilder changes = new StringBuilder();
        if (!newName.equals(originalStaff.getFullName())) changes.append("- Tên: ").append(originalStaff.getFullName()).append(" -> ").append(newName).append("\n");
        if (!newEmail.equals(originalStaff.getEmail())) changes.append("- Email: ").append(originalStaff.getEmail()).append(" -> ").append(newEmail).append("\n");
        if (!newDob.equals(originalStaff.getDoB())) changes.append("- Ngày sinh: ").append(originalStaff.getDoB()).append(" -> ").append(newDob).append("\n");
        if (!newPhone.equals(originalStaff.getPhoneNumber())) changes.append("- SĐT: ").append(originalStaff.getPhoneNumber()).append(" -> ").append(newPhone).append("\n");

        if (changes.length() == 0) {
            showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Bạn chưa thay đổi dữ liệu nào cả.");
            return;
        }

        //Gọi DAO
        boolean isSuccess = StaffInfoDAO.updateStaff(originalStaff.getStaffId(), newName, newEmail,  newDob, newPhone);

        if (isSuccess) {
            showAlert(Alert.AlertType.INFORMATION, "Cập nhật thành công!", "Các mục đã thay đổi:\n" + changes.toString());
            closeWindow(event);
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể cập nhật nhân viên.");
        }
    }

    @FXML
    void handleCancel(ActionEvent event) { closeWindow(event); }

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
