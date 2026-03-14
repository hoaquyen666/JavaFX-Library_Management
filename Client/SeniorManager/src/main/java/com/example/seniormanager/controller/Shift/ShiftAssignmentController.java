package com.example.seniormanager.controller.Shift;

import com.example.seniormanager.dao.ShiftDAO;
import com.example.seniormanager.model.ShiftOption;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

//CONTROLLER CHO MENU CON THÊM CA LÀM CHO NHÂN VIÊN
public class ShiftAssignmentController implements Initializable {
    @FXML private Label lblTitle;
    @FXML private Label lblStaffInfo;
    @FXML private Label lblDateInfo;
    @FXML private ComboBox<ShiftOption> cbShift;
    @FXML private CheckBox chkAttendance;

    //Các biến dữ liệu lấy từ Lịch đã làm
    private int currentLibrarianId;
    private LocalDate currentWorkDate;

    // Nếu assignmentId = -1 nghĩa là Thêm mới. Nếu > 0 nghĩa là Sửa.
    private int currentAssignmentId = -1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Load các ca làm từ DAO
        List<ShiftOption> shifts = ShiftDAO.getAllShifts();
        cbShift.getItems().addAll(shifts);
    }

    //Hàm để gọi từ Màn hình lịch
    public void setAssignmentData(int librarianId, String staffName, LocalDate date, int assignmentId, int currentShiftId, boolean isAttended) {
        //Lấy dữ liệu từ lịch gán vào
        this.currentLibrarianId = librarianId; //Lấy id nhân viên
        this.currentWorkDate = date; //Lấy ngày làm
        this.currentAssignmentId = assignmentId;

        lblStaffInfo.setText("Nhân viên: " + staffName);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        lblDateInfo.setText("Ngày làm việc: "+ date.format(formatter));

        if(assignmentId == -1){
            //Thêm ca làm
            lblTitle.setText("Thêm ca làm mới");
            chkAttendance.setVisible(false);
        }
        else {
            //Sửa
            lblTitle.setText("Chỉnh Sửa Ca Làm");
            chkAttendance.setVisible(true);
            chkAttendance.setSelected(isAttended);

            // Tự động chọn đúng Ca cũ trong ComboBox
            for (ShiftOption shift : cbShift.getItems()) {
                if (shift.getShiftId() == currentShiftId) {
                    cbShift.setValue(shift);
                    break;
                }
            }
        }

    }

    @FXML
    void handleSave(ActionEvent event){
        ShiftOption selectedShift = cbShift.getValue(); //Lấy dữ liệu vc làm
        if(selectedShift == null){
            showAlert(Alert.AlertType.WARNING, "Lỗi", "Vui lòng chọn một ca làm!");
            return;
        }

        boolean isSuccess;
        if(currentAssignmentId == -1){
            //Thêm mới gửi đến file DAO để thêm database
            isSuccess = ShiftDAO.assignShift(currentLibrarianId, selectedShift.getShiftId(), currentWorkDate, 10);
        }
        else {
            isSuccess = ShiftDAO.updateShift(currentAssignmentId, selectedShift.getShiftId(), chkAttendance.isSelected());
        }

        if (isSuccess) {
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã lưu thông tin ca làm!");
            closeWindow(event);
        } else {
            showAlert(Alert.AlertType.ERROR, "Thất bại", "Có lỗi xảy ra khi lưu vào CSDL.");
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
