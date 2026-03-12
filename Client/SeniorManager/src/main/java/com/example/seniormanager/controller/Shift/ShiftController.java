package com.example.seniormanager.controller.Shift;

import com.example.seniormanager.dao.ShiftDAO;
import com.example.seniormanager.dao.StaffInfoDAO;
import com.example.seniormanager.model.ShiftAssignment;
import com.example.seniormanager.model.StaffOption;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ShiftController implements Initializable {
    @FXML private ComboBox<StaffOption> cbStaff;
    @FXML private Label lblMonthYear;
    @FXML private GridPane headerGrid;
    @FXML private GridPane calendarGrid;

    private YearMonth currentYearMonth;
    //Lấy ca làm cho tháng hiện tại
    private List<ShiftAssignment> currentMonthShifts = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentYearMonth = YearMonth.now();

        //Vẽ header thứ trong tuần
        setupDaysOfWeek();

        //Tải danh sách nhận viên
        cbStaff.getItems().addAll(StaffInfoDAO.getLibrarianOptions());

        //Bắt sự kiện, hễ chọn nhân viện khác -> tự tải lịch
        cbStaff.setOnAction(event -> renderCalendar());

        //Vẽ lưới lịch
        renderCalendar();
    }

    private void setupDaysOfWeek(){
        String[] days = {"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5 ", "Thứ 6", "Thứ 7", "Chủ nhật"};
        for (int i = 0; i < 7; i++) {
            Label label = new Label(days[i]);
            label.getStyleClass().add("day-header");
            VBox box = new VBox(label);
            box.setAlignment(Pos.CENTER);
            headerGrid.add(box, i, 0); //Hàng 0
        }
    }

    private void renderCalendar(){
        calendarGrid.getChildren().clear();//Xoá lịch cũ

        //Set label
        lblMonthYear.setText("Tháng " + currentYearMonth.getMonthValue() + "/" + currentYearMonth.getYear());

        //
        StaffOption selectedStaff = cbStaff.getValue();
        if(selectedStaff != null){
            currentMonthShifts = ShiftDAO.getShiftsByMonth(
                    selectedStaff.getStaffId(),
                    currentYearMonth.getMonthValue(),
                    currentYearMonth.getYear()
            );
        }
        else {
            currentMonthShifts.clear();
        }

        // Lấy ngày đầu tiên của tháng và xem nó rơi vào thứ mấy (1 = Thứ 2, 7 = Chủ Nhật)
        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int dayOfWeekOfFirst = firstOfMonth.getDayOfWeek().getValue(); //Lấy thứ trong ngày đầu tiên
        int daysInMonth = currentYearMonth.lengthOfMonth(); //số ngày trong tháng hiện tại

        int col = dayOfWeekOfFirst -1;
        int row = 0;

        //Vẽ Từng ngày
        for (int day = 1; day <= daysInMonth ; day++) {
            LocalDate date = currentYearMonth.atDay(day); //Lấy ra ngày

            //Tạo 1 cell đại diện cho 1 ngày
            VBox dayCell = createDayCell(date);

            //Đưa ô vào lịch
            calendarGrid.add(dayCell, col, row);

            col++; //Tăng cột lên
            if(col > 6){
                //Nếu cột > 6 tức hết tuần
                col = 0;
                row++;
            }
        }
    }

    //Tạo ô ngày cho lịch
    private VBox createDayCell(LocalDate date){
        VBox cell = new VBox(5);
        cell.setAlignment(Pos.TOP_RIGHT);
        //Style cho ô
//        cell.setStyle(("-fx-background-color: #f8fafc; -fx-border-color: #e2e8f0; -fx-border-width: 1; -fx-padding: 5; -fx-background-radius: 5"));
        cell.setPrefHeight(100); // Đặt chiều cao tĩnh cho ô;

        Label lblDate = new Label(String.valueOf(date.getDayOfMonth())); //lấy ra ngày
        lblDate.getStyleClass().add("date-label");
        cell.getChildren().add(lblDate);

        //Gọi model
        ShiftAssignment shiftForDate = currentMonthShifts.stream()
                .filter(s -> s.getWorkDate().equals(date)) //lấy ra ngày
                .findFirst()
                .orElse(null);


        boolean hasShift = (shiftForDate != null);

        //Nếu đã có ca làm ngày đó
        if (hasShift) {
            cell.getStyleClass().add("day-cell-has-shift");

            //Lấy ra ngày và format
            String startTimeStr = shiftForDate.getStartTime().toString().substring(0, 5);
            String endTimeStr = shiftForDate.getEndTime().toString().substring(0, 5);
            String timeString = "(" + startTimeStr + " - " + endTimeStr + ")";

            // Hiển thị tên Ca làm, Giờ làm và Trạng thái điểm danh
            String status = shiftForDate.isAttendanceStatus() ? "\n[ Có mặt ]" : "\n[ Chưa điểm danh ]";

            // Gộp tất cả lại thành 1 chuỗi
            Label lblShiftInfo = new Label(shiftForDate.getShiftName() + timeString + status);
            lblShiftInfo.getStyleClass().add("shift-info-label");

            if (shiftForDate.isAttendanceStatus()) {
                lblShiftInfo.setStyle("-fx-text-fill: #16a34a;"); // Màu xanh lá nếu đã điểm danh
            }

            cell.getChildren().add(lblShiftInfo);
        } else {
            cell.getStyleClass().add("day-cell");
        }

        //Bắt sự kiện click chuột
        cell.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                //Tải staff đang chọn
                StaffOption selectedStaff = cbStaff.getValue();
                if (selectedStaff == null) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Vui lòng chọn nhân viên ở góc trên trước khi phân công!");
                    alert.showAndWait();
                    return;
                }

                int librarianId = selectedStaff.getStaffId(); //Lấy ra ID nhân viên đó
                String staffName = selectedStaff.toString(); // VD: NV01 - Nguyễn Văn A

                try {
                    //Chạy menu thông tin ca làm
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/seniormanager/staff-management/shift-view/ShiftAssignmentForm.fxml"));
                    Parent root = loader.load();
                    //Lấy controller từ menu pop lên
                    ShiftAssignmentController controller = loader.getController();

                    if (hasShift) {
                        // NẾU CÓ CA: Truyền toàn bộ thông tin đã lấy từ sang
                        controller.setAssignmentData(librarianId, staffName, date,
                                shiftForDate.getAssignmentId(),
                                shiftForDate.getShiftId(),
                                shiftForDate.isAttendanceStatus());
                    } else {
                        // NẾU CHƯA CÓ CA: Truyền -1 để Form hiểu là Thêm Mới
                        controller.setAssignmentData(librarianId, staffName, date, -1, 0, false);
                    }

                    Stage stage = new Stage();
                    stage.setTitle("Phân công ca làm");
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.setScene(new Scene(root));
                    stage.showAndWait();

                    // Tự động load lại lịch sau khi tắt popup
                    renderCalendar();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Hiệu ứng Hover cho ô
        cell.setOnMouseEntered(e -> cell.setOpacity(0.8));
        cell.setOnMouseExited(e -> cell.setOpacity(1.0));

        return cell;
    }

    @FXML
    void prevMonth(ActionEvent event){
        currentYearMonth = currentYearMonth.minusMonths(1);
        renderCalendar();
    }
    @FXML
    void nextMonth(ActionEvent event){
        currentYearMonth = currentYearMonth.plusMonths(1);
        renderCalendar();
    }


}
