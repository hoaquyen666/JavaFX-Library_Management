package com.example.librarian.controller;

import com.example.librarian.dao.ReturnDAO;
import com.example.librarian.model.ReturnDetailDTO;
import com.example.librarian.util.MessageBox;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class PaymentPopupController {
    @FXML
    private Label lblReaderName;
    @FXML private Label lblBookInfo;
    @FXML private Label lblDeposit;
    @FXML private Label lblOverdueDaysText;
    @FXML private Label lblFineAmount;
    @FXML private Label lblTotalText;
    @FXML private Label lblTotalAmount;

    @FXML private RadioButton radioCash;
    @FXML private RadioButton radioQR;
    @FXML private VBox qrContainer;
    @FXML private ImageView imgQRCode;
    @FXML private Button btnConfirmPayment;

    private ReturnDetailDTO returnData;
    private ReturnManagementController parentController;
    private ReturnDAO returnDAO = new ReturnDAO();

    private double calculatedFine = 0;
    private String newStatus = "Returned";

    private DecimalFormat df = new DecimalFormat("#,### đ");

    public void setReturnData(ReturnDetailDTO data, ReturnManagementController parentController){
        this.returnData = data;
        this.parentController = parentController;

        //Set dữ liệu lên form
        lblReaderName.setText(data.getReaderName() + " (ID: " + data.getReaderId() + ")");
        lblBookInfo.setText(data.getBookName() + " - " + data.getCopyId());
        lblDeposit.setText(df.format(data.getDepositAmount()));

        calculatedFineAndTotal();
    }

    //Hàm tính tiền phạt hoặc tổng tiền cần phải trả lại reader
    private void calculatedFineAndTotal(){
        LocalDate today = LocalDate.now();
        LocalDate dueDate = returnData.getDueDate();

        long daysLate = 0; //số ngày muộn

        if(dueDate !=null && today.isAfter(dueDate)){//Nếu ngày trả hiện tại sau hạn phải trả
            daysLate = ChronoUnit.DAYS.between(dueDate, today);
            newStatus = "Quá hạn";

            if(daysLate < 10){
                //Trễ dưới 10 ngày: 10k / ngày
                calculatedFine = daysLate * 10000.0;
            }else {
                //10 ngày trở lên: 1/3 tiền sách (Lấy tiền cọc nhân 2 chia 3)
                calculatedFine = (returnData.getDepositAmount() * 2.0) / 3.0;
            }

            // Cập nhật UI hiển thị Phạt
            if (daysLate > 0) {
                lblOverdueDaysText.setText("Trễ hạn (" + daysLate + " ngày):");
                lblFineAmount.setText("- " + df.format(calculatedFine));
            } else {
                lblOverdueDaysText.setText("Trễ hạn (0 ngày):");
                lblFineAmount.setText("- 0 đ");
                lblOverdueDaysText.setStyle("-fx-text-fill: #28a745;"); // Trả đúng hạn hiện màu xanh
                lblFineAmount.setStyle("-fx-text-fill: #28a745;");
            }

            // TÍNH TỔNG TIỀN VÀ CẬP NHẬT UI
            double total = returnData.getDepositAmount() - calculatedFine; //Tiền cọc trừ đi tiền phạt

            if (total >= 0) {
                lblTotalText.setText("Thư viện hoàn trả:");
                lblTotalAmount.setText(df.format(total));
                lblTotalAmount.setStyle("-fx-text-fill: #28a745;"); // Xanh lá
            } else {
                lblTotalText.setText("Độc giả cần nộp thêm:");
                lblTotalAmount.setText(df.format(Math.abs(total)));
                lblTotalAmount.setStyle("-fx-text-fill: #d9534f;"); // Đỏ
            }

        }
    }
    //Hàm Hiển thị QR
    @FXML
    void handlePaymentMethodChange() {
        if (radioQR.isSelected()) {
            // Hiển thị khung QR Code
            qrContainer.setVisible(true);
            qrContainer.setManaged(true);

            try {
                imgQRCode.setImage(new Image(getClass().getResourceAsStream("/images/img_1.png")));
            } catch (Exception e) {
                System.out.println("Không tìm thấy ảnh QR mẫu!");
            }
        } else {
            // Ẩn khung QR Code
            qrContainer.setVisible(false);
            qrContainer.setManaged(false);
        }
    }

    @FXML
    void handleConfirmPayment() {
        // Gọi DAO để cập nhật trạng thái phiếu mượn (Trả sách thành công)
        boolean success = returnDAO.processSingleReturn(returnData.getBorrowId(), returnData.getCopyId(), calculatedFine, newStatus);
        MessageBox ms = new MessageBox();
        if (success) {
            ms.showInfo("Đã Trả và thanh toán thành công");

            // Đóng popup
            Stage stage = (Stage) btnConfirmPayment.getScene().getWindow();
            stage.close();

            // Yêu cầu màn hình chính (ReturnManagementController) load lại bảng
            if (parentController != null) {
                parentController.loadData();
            }
        } else {
            MessageBox.showError("Lỗi DB", "Có lỗi xảy ra khi cập nhật DB");
        }
    }

    @FXML
    void handleCancel() {
        Stage stage = (Stage) btnConfirmPayment.getScene().getWindow();
        stage.close();
    }
}
