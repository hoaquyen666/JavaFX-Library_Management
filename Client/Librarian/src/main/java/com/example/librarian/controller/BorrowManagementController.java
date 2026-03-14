package com.example.librarian.controller;

import com.example.librarian.dao.BorrowDAO;
import com.example.librarian.model.Borrow;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.scene.control.DatePicker;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class BorrowManagementController implements Initializable {

    @FXML private TableView<Borrow> borrowTable;

    // Khai báo các cột
    @FXML private TableColumn<Borrow, Integer> colStt;
    @FXML private TableColumn<Borrow, Integer> colBorrowId;
    @FXML private TableColumn<Borrow, String> colBorrowCode;
    @FXML private TableColumn<Borrow, String> colReaderCode;
    @FXML private TableColumn<Borrow, String> colStaffCode;
    @FXML private TableColumn<Borrow, String> colBorrowDate;
    @FXML private TableColumn<Borrow, String> colDueDate;
    @FXML private TableColumn<Borrow, Integer> colQuantity;
    @FXML private TableColumn<Borrow, String> colStatus;

    private BorrowDAO borrowDAO = new BorrowDAO();

    @FXML private StackPane addPopupOverlay;
    @FXML private javafx.scene.control.Label lblPopupTitle;
    @FXML private javafx.scene.control.TextField txtPopupReaderCode;
    @FXML private javafx.scene.control.TextField txtPopupStaffCode;
    @FXML private DatePicker dpPopupDueDate;
    @FXML private javafx.scene.control.TextField txtPopupBarcodes;

    private Borrow editingBorrow = null;

    @FXML private javafx.scene.control.ComboBox<String> cbStatusFilter;
    @FXML private javafx.scene.control.ComboBox<String> cbDateFilter;

    @FXML private javafx.scene.control.TextField txtSearch;
    @FXML private javafx.scene.control.Label lblTotalBorrows;

    @FXML private javafx.scene.control.Button btnPrevPage;
    @FXML private javafx.scene.control.Button btnNextPage;
    @FXML private javafx.scene.control.Label lblPageInfo;

    private final int ITEMS_PER_PAGE = 10;
    private int currentPage = 0;

    private List<Borrow> allBorrowsFromDB;
    private List<Borrow> filteredBorrows;
    public static boolean openAddPopupOnLoad = false;

    @FXML
    void handleAddBorrow() {
        editingBorrow = null; // Chế độ Thêm mới
        lblPopupTitle.setText("Tạo Phiếu Mượn Mới");

        txtPopupReaderCode.clear();
        txtPopupStaffCode.clear();
        dpPopupDueDate.setValue(LocalDate.now().plusDays(7)); // Mặc định cho mượn 7 ngày
        txtPopupBarcodes.clear();
        txtPopupBarcodes.setDisable(false); // Cho phép nhập mã vạch

        addPopupOverlay.setVisible(true);
    }

    @FXML
    void handleEditBorrow() {
        Borrow selectedBorrow = borrowTable.getSelectionModel().getSelectedItem();
        if (selectedBorrow == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn một phiếu mượn để sửa!");
            return;
        }

        //chế độ Sửa phiếu
        editingBorrow = selectedBorrow;
        lblPopupTitle.setText("Sửa Phiếu Mượn: " + selectedBorrow.getBorrowCode());

        // Đổ dữ liệu cũ vào form
        txtPopupReaderCode.setText(selectedBorrow.getReaderCode());
        txtPopupStaffCode.setText(selectedBorrow.getStaffCode());

        // Parse ngày từ String (VD: 20/03/2026) sang LocalDate cho cái DatePicker
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            LocalDate date = LocalDate.parse(selectedBorrow.getDueDate(), formatter);
            dpPopupDueDate.setValue(date);
        } catch (Exception e) {
            dpPopupDueDate.setValue(LocalDate.now());
        }

        // Khi sửa phiếu thì tạm thời khóa ô nhập mã vạch (Vì sách đã mượn rồi, việc trả sách/xóa sách sẽ làm ở chức năng Trả sách riêng)
        txtPopupBarcodes.setText("Không thể sửa danh sách sách ở đây");
        txtPopupBarcodes.setDisable(true);

        addPopupOverlay.setVisible(true);
    }

    @FXML
    void handleClosePopup() {
        addPopupOverlay.setVisible(false);
    }

    @FXML
    void handleSaveBorrow() {
        // 1. Lấy dữ liệu từ form
        String readerCode = txtPopupReaderCode.getText().trim();
        String staffCode = txtPopupStaffCode.getText().trim();
        LocalDate dueDate = dpPopupDueDate.getValue();
        String barcodesStr = txtPopupBarcodes.getText().trim();

        // 2. Validate bỏ trống
        if (readerCode.isEmpty() || staffCode.isEmpty() || dueDate == null || (editingBorrow == null && barcodesStr.isEmpty())) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng điền đầy đủ thông tin!");
            return;
        }

        // Chuyển ngày trên lịch thành chuỗi chuẩn MySQL (Mặc định bắt trả sách lúc 17:00:00)
        String dueDateStr = dueDate.toString() + " 17:00:00";

        if (editingBorrow == null) {
            // TẠO PHIẾU MỚI
            // Tự động sinh mã phiếu mượn (Ví dụ: PM-9823)
            String borrowCode = "PM-" + (System.currentTimeMillis() % 10000);

            // Tách chuỗi "BC001, BC002" thành danh sách List
            List<String> barcodes = java.util.Arrays.asList(barcodesStr.split(","));

            // Đóng gói dữ liệu vào Object Borrow
            Borrow newBorrow = new Borrow(0, borrowCode, readerCode, staffCode, null, dueDateStr, 0, "Borrowing");

            // Đẩy xuống DAO
            boolean isSuccess = borrowDAO.insertBorrowWithDetails(newBorrow, barcodes);

            if (isSuccess) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã tạo phiếu mượn và xuất sách khỏi kho!");
                handleClosePopup();
                loadDataToTable(); // Load lại bảng
            } else {
                showAlert(Alert.AlertType.ERROR, "Thất bại", "Lỗi tạo phiếu! Kiểm tra lại mã Độc giả/Nhân viên hoặc xem sách này đã có người mượn chưa.");
            }

        } else {

            // Cập nhật dữ liệu mới từ form vào object đang sửa
            editingBorrow.setReaderCode(readerCode);
            editingBorrow.setStaffCode(staffCode);
            editingBorrow.setDueDate(dueDateStr);

            // Đẩy xuống Database qua DAO
            boolean isSuccess = borrowDAO.updateBorrow(editingBorrow);

            if (isSuccess) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã cập nhật thông tin phiếu mượn!");
                handleClosePopup();
                loadDataToTable();     // Kéo data mới từ DB
                borrowTable.refresh(); // Ép giao diện vẽ lại
            } else {
                showAlert(Alert.AlertType.ERROR, "Thất bại", "Không thể cập nhật! Kiểm tra lại mã Độc giả hoặc Nhân viên có tồn tại hay không.");
            }
        }
    }

    @FXML
    void handlePrevPage() {
        if (currentPage > 0) {
            currentPage--;
            updateTablePagination();
        }
    }

    @FXML
    void handleNextPage() {
        int totalPages = (int) Math.ceil((double) filteredBorrows.size() / ITEMS_PER_PAGE);
        if (currentPage < totalPages - 1) {
            currentPage++;
            updateTablePagination();
        }
    }

    // Hàm gọi thông báo phụ trợ
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // số thứ tự
        colStt.setCellValueFactory(column -> {
            int index = borrowTable.getItems().indexOf(column.getValue());
            return new ReadOnlyObjectWrapper<>((currentPage * ITEMS_PER_PAGE) + index + 1);
        });

        // Mapping các cột với các biến trong class Borrow
        colBorrowId.setCellValueFactory(new PropertyValueFactory<>("borrowId"));
        colBorrowCode.setCellValueFactory(new PropertyValueFactory<>("borrowCode"));
        colReaderCode.setCellValueFactory(new PropertyValueFactory<>("readerCode"));
        colStaffCode.setCellValueFactory(new PropertyValueFactory<>("staffCode"));
        colBorrowDate.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        colDueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        //Ô Trạng thái
        cbStatusFilter.getItems().addAll("Tất cả trạng thái", "Đang mượn", "Đã trả", "Quá hạn");
        cbStatusFilter.setValue("Tất cả trạng thái");

        //Ô Thời gian / Hẹn trả
        cbDateFilter.getItems().addAll("Tất cả thời gian", "Hôm nay", "3 ngày tới", "7 ngày tới");
        cbDateFilter.setValue("Tất cả thời gian");

        // Bắt sự kiện thay đổi để tự động Lọc
        txtSearch.textProperty().addListener((obs, old, newVal) -> filterData());
        cbStatusFilter.valueProperty().addListener((obs, old, newVal) -> filterData());
        cbDateFilter.valueProperty().addListener((obs, old, newVal) -> filterData());

        if (openAddPopupOnLoad) {
            javafx.application.Platform.runLater(() -> {
                handleAddBorrow();
            });
            openAddPopupOnLoad = false;
        }
        addPopupOverlay.setOnMouseClicked(event -> {
            if (event.getTarget() == addPopupOverlay) {
                handleClosePopup();
            }
        });
        // Load dữ liệu lên bảng
        loadDataToTable();
    }

    private void loadDataToTable() {
        // Chỉ kéo từ Database lên 1 lần duy nhất
        allBorrowsFromDB = borrowDAO.getAllBorrows();
        filterData();

        // highlight cho status
        colStatus.setCellFactory(column -> {
            return new javafx.scene.control.TableCell<Borrow, String>() {
                private final javafx.scene.control.Label statusLabel = new javafx.scene.control.Label();

                {
                    // Ép cái Label luôn nằm chính giữa ô
                    setAlignment(javafx.geometry.Pos.CENTER);
                }
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setGraphic(null);
                    } else {
                        String baseStyle = "-fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 5; -fx-pref-width: 90; -fx-alignment: CENTER;";
                        // Kiểm tra giá trị từ DB và đổ màu nền
                        switch (item) {
                            case "Borrowing":
                                statusLabel.setText("Đang mượn");
                                statusLabel.setStyle(baseStyle + "-fx-background-color: #31b865;"); // Màu xanh lá
                                break;
                            case "Overdue":
                                statusLabel.setText("Quá hạn");
                                statusLabel.setStyle(baseStyle + "-fx-background-color: #ed3736;"); // Màu đỏ san hô
                                break;
                            case "Returned":
                                statusLabel.setText("Đã trả");
                                statusLabel.setStyle(baseStyle + "-fx-background-color: #b2bec3;"); // Màu xám tro
                                break;
                            default:
                                statusLabel.setText(item);
                                statusLabel.setStyle(baseStyle + "-fx-background-color: #636e72;");
                                break;
                        }

                        setGraphic(statusLabel);
                    }
                }
            };
        });
    }
    private void filterData() {
        if (allBorrowsFromDB == null) return;

        String searchText = txtSearch.getText().toLowerCase().trim();
        String selectedStatus = cbStatusFilter.getValue();
        String selectedDate = cbDateFilter.getValue();

        filteredBorrows = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Borrow b : allBorrowsFromDB) {
            boolean matchSearch = true;
            boolean matchStatus = true;
            boolean matchDate = true;

            // 1. Màng lọc Text (So khớp Mã phiếu, Mã Độc giả, Mã Nhân viên)
            if (!searchText.isEmpty()) {
                String borrowCode = b.getBorrowCode() != null ? b.getBorrowCode().toLowerCase() : "";
                String readerCode = b.getReaderCode() != null ? b.getReaderCode().toLowerCase() : "";
                String staffCode = b.getStaffCode() != null ? b.getStaffCode().toLowerCase() : "";

                if (!borrowCode.contains(searchText) && !readerCode.contains(searchText) && !staffCode.contains(searchText)) {
                    matchSearch = false;
                }
            }

            // 2. Màng lọc Trạng thái (Dịch từ Tiếng Việt sang DB Tiếng Anh)
            if (selectedStatus != null && !selectedStatus.equals("Tất cả trạng thái")) {
                String dbStatus = b.getStatus(); // "Borrowing", "Returned", "Overdue"
                if (selectedStatus.equals("Đang mượn") && !dbStatus.equals("Borrowing")) matchStatus = false;
                if (selectedStatus.equals("Đã trả") && !dbStatus.equals("Returned")) matchStatus = false;
                if (selectedStatus.equals("Quá hạn") && !dbStatus.equals("Overdue")) matchStatus = false;
            }

            // 3. Màng lọc Ngày tháng (Đếm số ngày còn lại)
            if (selectedDate != null && !selectedDate.equals("Tất cả thời gian")) {
                try {
                    LocalDate dueDate = LocalDate.parse(b.getDueDate(), formatter);
                    long daysBetween = ChronoUnit.DAYS.between(today, dueDate);

                    if (selectedDate.equals("Hôm nay") && daysBetween != 0) matchDate = false;
                    if (selectedDate.equals("3 ngày tới") && (daysBetween < 0 || daysBetween > 3)) matchDate = false;
                    if (selectedDate.equals("7 ngày tới") && (daysBetween < 0 || daysBetween > 7)) matchDate = false;
                } catch (Exception e) {
                }
            }

            if (matchSearch && matchStatus && matchDate) {
                filteredBorrows.add(b);
            }
        }

        currentPage = 0;
        updateTablePagination();
    }
    // Hàm cắt dữ liệu hiển thị theo trang
    private void updateTablePagination() {
        if (filteredBorrows == null || filteredBorrows.isEmpty()) {
            borrowTable.setItems(FXCollections.observableArrayList());
            lblTotalBorrows.setText("0 phiếu mượn");
            lblPageInfo.setText("Trang 0 / 0");
            btnPrevPage.setDisable(true);
            btnNextPage.setDisable(true);
            return;
        }

        int totalItems = filteredBorrows.size();
        int totalPages = (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE);

        if (currentPage >= totalPages) currentPage = totalPages - 1;
        if (currentPage < 0) currentPage = 0;

        int startIndex = currentPage * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, totalItems);

        List<Borrow> pageData = filteredBorrows.subList(startIndex, endIndex);
        borrowTable.setItems(FXCollections.observableArrayList(pageData));

        lblTotalBorrows.setText(totalItems + " phiếu mượn");
        lblPageInfo.setText("Trang " + (currentPage + 1) + " / " + totalPages);

        btnPrevPage.setDisable(currentPage == 0); // Đang ở trang 1 thì khóa nút Prev
        btnNextPage.setDisable(currentPage == totalPages - 1); // Đang ở trang cuối thì khóa nút Next
    }



}
