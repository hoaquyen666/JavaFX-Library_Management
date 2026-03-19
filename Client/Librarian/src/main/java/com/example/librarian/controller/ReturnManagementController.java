package com.example.librarian.controller;

import com.example.librarian.dao.BorrowDAO;
import com.example.librarian.dao.ReturnDAO;
import com.example.librarian.model.Borrow;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.image.ImageView;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class ReturnManagementController implements Initializable {

    // Các thành phần bảng
    @FXML private TableView<Borrow> returnTable;
    @FXML private TableColumn<Borrow, Integer> colStt, colBorrowId, colQuantity;
    @FXML private TableColumn<Borrow, String> colBorrowCode, colReaderCode, colStaffCode, colBorrowDate, colDueDate, colStatus;
    @FXML private TextField txtSearch;
    @FXML private Label lblTotalReturns, lblPageInfo;
    @FXML private Button btnPrevPage, btnNextPage;

    // Các thành phần Popup kết quả
    @FXML private StackPane resultPopupOverlay;
    @FXML private Label lblResCode, lblResReader, lblResStatus;
    @FXML private ImageView imgQrCode;

    private BorrowDAO borrowDAO = new BorrowDAO();
    private ReturnDAO returnDAO = new ReturnDAO();
    private List<Borrow> allData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadData();
    }

    private void setupTableColumns() {
        colStt.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(returnTable.getItems().indexOf(c.getValue()) + 1));
        colBorrowId.setCellValueFactory(new PropertyValueFactory<>("borrowId"));
        colBorrowCode.setCellValueFactory(new PropertyValueFactory<>("borrowCode"));
        colReaderCode.setCellValueFactory(new PropertyValueFactory<>("readerCode"));
        colStaffCode.setCellValueFactory(new PropertyValueFactory<>("staffCode"));
        colBorrowDate.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        colDueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colStatus.setCellFactory(column -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) setGraphic(null);
                else {
                    Label lbl = new Label(item.equals("Borrowing") ? "Đang mượn" : (item.equals("Overdue") ? "Quá hạn" : "Đã trả"));
                    String color = item.equals("Borrowing") ? "#31b865" : (item.equals("Overdue") ? "#ed3736" : "#b2bec3");
                    lbl.setStyle("-fx-text-fill: white; -fx-background-color: " + color + "; -fx-padding: 4 10; -fx-background-radius: 5; -fx-pref-width: 90; -fx-alignment: CENTER;");
                    setGraphic(lbl);
                }
            }
        });
    }

    private void loadData() {
        allData = borrowDAO.getAllBorrows();
        returnTable.setItems(FXCollections.observableArrayList(allData));
        lblTotalReturns.setText(allData.size() + " phiếu");
    }

    @FXML
    void handleReturnAction() {
        Borrow selected = returnTable.getSelectionModel().getSelectedItem();
        if (selected == null || !"Borrowing".equals(selected.getStatus())) {
            new Alert(Alert.AlertType.WARNING, "Vui lòng chọn phiếu đang mượn!").show();
            return;
        }

        DatePicker dp = new DatePicker(LocalDate.now());
        VBox box = new VBox(10, new Label("Ngày trả thực tế:"), dp);
        box.setPadding(new Insets(15));

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Xác nhận");
        confirmDialog.setHeaderText("Kiểm tra thông tin trả sách cho phiếu: " + selected.getBorrowCode());
        confirmDialog.getDialogPane().setContent(box);

        confirmDialog.showAndWait().ifPresent(res -> {
            if (res == ButtonType.OK && dp.getValue() != null) {
                try {
                    // Logic: So sánh ngày trả thực tế với Hạn trả
                    // Chú ý: Pattern phải khớp với chuỗi ngày trong bảng (thường là dd/MM/yyyy HH:mm)
                    String dueDateStr = selected.getDueDate().split(" ")[0]; // Lấy phần ngày
                    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDate dueDate = LocalDate.parse(dueDateStr, fmt);

                    String newStatus = dp.getValue().isAfter(dueDate) ? "Overdue" : "Returned";

                    List<Integer> ids = returnDAO.getCopyIdsByBorrowId(selected.getBorrowId());

                    if (returnDAO.processReturn(selected.getBorrowId(), ids, newStatus)) {
                        // Hiển thị thông tin lên Popup kết quả
                        lblResCode.setText(selected.getBorrowCode());
                        lblResReader.setText(selected.getReaderCode());

                        if ("Overdue".equals(newStatus)) {
                            lblResStatus.setText("QUÁ HẠN (Cần nộp phạt)");
                            lblResStatus.setStyle("-fx-text-fill: #ed3736; -fx-font-weight: bold;");
                        } else {
                            lblResStatus.setText("ĐÃ TRẢ (Đúng hạn)");
                            lblResStatus.setStyle("-fx-text-fill: #31b865; -fx-font-weight: bold;");
                        }

                        resultPopupOverlay.setVisible(true);
                        loadData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Lỗi định dạng ngày tháng!").show();
                }
            }
        });
    }

    @FXML
    void handleCloseResultPopup() {
        resultPopupOverlay.setVisible(false);
    }
}