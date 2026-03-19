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
    @FXML private javafx.scene.control.ComboBox<String> cbPopupReader;
    @FXML private javafx.scene.control.ComboBox<String> cbPopupStaff;
    @FXML private DatePicker dpPopupDueDate;
    @FXML private javafx.scene.control.TextField txtPopupCopyIds;

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

    //popup cho bảng chi tiết phiếu mượn
    @FXML private StackPane detailPopupOverlay;
    @FXML private javafx.scene.control.Label lblDetailTitle;
    @FXML private javafx.scene.control.Label lblDetailReader;
    @FXML private javafx.scene.control.Label lblDetailStaff;
    @FXML private javafx.scene.control.Label lblDetailTime;
    @FXML private javafx.scene.control.ListView<String> lvDetailBooks;

    @FXML
    void handleAddBorrow() {
        editingBorrow = null;
        lblPopupTitle.setText("Tạo Phiếu Mượn Mới");

        // Đổ dữ liệu mới nhất từ DB lên ComboBox
        cbPopupReader.setItems(FXCollections.observableArrayList(borrowDAO.getReaderListForCombo()));
        cbPopupStaff.setItems(FXCollections.observableArrayList(borrowDAO.getStaffListForCombo()));

        cbPopupReader.setValue(null);
        cbPopupStaff.setValue(null);
        dpPopupDueDate.setValue(LocalDate.now().plusDays(7));
        txtPopupCopyIds.clear();
        txtPopupCopyIds.setDisable(false);

        addPopupOverlay.setVisible(true);
    }

    @FXML
    void handleEditBorrow() {
        Borrow selectedBorrow = borrowTable.getSelectionModel().getSelectedItem();
        if (selectedBorrow == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn một phiếu mượn để sửa!");
            return;
        }

        String status = selectedBorrow.getStatus();
        if ("Returned".equalsIgnoreCase(status) || "Đã trả".equalsIgnoreCase(status)) {
            showAlert(Alert.AlertType.ERROR, "Đã khóa quyền chỉnh sửa", "Phiếu mượn này đã hoàn tất giao dịch. Không thể chỉnh sửa dữ liệu lịch sử!");
            return;
        }
        editingBorrow = selectedBorrow;
        lblPopupTitle.setText("Sửa Phiếu Mượn: " + selectedBorrow.getBorrowCode());

        //load lại danh sách vào ComboBox trước
        cbPopupReader.setItems(FXCollections.observableArrayList(borrowDAO.getReaderListForCombo()));
        cbPopupStaff.setItems(FXCollections.observableArrayList(borrowDAO.getStaffListForCombo()));


        for (String item : cbPopupReader.getItems()) {
            if (item != null && item.contains(selectedBorrow.getReaderCode())) {
                cbPopupReader.setValue(item);
                break;
            }
        }

        //Tìm và chọn đúng Nhân viên
        for (String item : cbPopupStaff.getItems()) {
            if (item != null && item.contains(selectedBorrow.getStaffCode())) {
                cbPopupStaff.setValue(item);
                break;
            }
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            LocalDate date = LocalDate.parse(selectedBorrow.getDueDate(), formatter);
            dpPopupDueDate.setValue(date);
        } catch (Exception e) {
            dpPopupDueDate.setValue(LocalDate.now());
        }


        txtPopupCopyIds.setText("Không thể sửa danh sách sách ở đây");
        txtPopupCopyIds.setDisable(true);

        addPopupOverlay.setVisible(true);
    }

    @FXML
    void handleClosePopup() {
        addPopupOverlay.setVisible(false);
    }

    @FXML
    void handleSaveBorrow() {
        String selectedReader = cbPopupReader.getValue();
        String selectedStaff = cbPopupStaff.getValue();
        LocalDate dueDate = dpPopupDueDate.getValue();
        String copyIdsStr = txtPopupCopyIds.getText().trim();

        // Kiểm tra bỏ trống
        if (selectedReader == null || selectedStaff == null || dueDate == null || (editingBorrow == null && copyIdsStr.isEmpty())) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng điền đầy đủ thông tin!");
            return;
        }
        // CHỐT CHẶN: Kiểm tra ngày hẹn trả phải từ ngày mai trở đi
        if (!dueDate.isAfter(LocalDate.now())) {
            showAlert(Alert.AlertType.WARNING, "Ngày không hợp lệ", "Ngày hẹn trả phải từ ngày mai trở đi!");
            return;
        }

        String dueDateStr = dueDate.toString() + " 17:00:00";

        if (editingBorrow == null) {
            try {
                int readerId = Integer.parseInt(selectedReader.split(" - ")[0]);
                int staffId = Integer.parseInt(selectedStaff.split(" - ")[0]);

                // BƯỚC 1: Lấy danh sách ID sách và kiểm tra xem có gõ trùng số không
                List<Integer> copyIds = new ArrayList<>();
                for (String idStr : copyIdsStr.split(",")) {
                    int id = Integer.parseInt(idStr.trim());
                    if (copyIds.contains(id)) {
                        showAlert(Alert.AlertType.WARNING, "Lỗi nhập liệu", "Bạn đang nhập trùng ID " + id + " nhiều lần!");
                        return; // Bắt buộc phải dừng lại ngay
                    }
                    copyIds.add(id);
                }

                // BƯỚC 2: CHỐT CHẶN BẢO VỆ (Phải kiểm tra TRƯỚC KHI TẠO PHIẾU)
                String checkStatus = borrowDAO.checkCopiesAvailable(copyIds);
                if (!checkStatus.equals("OK")) {
                    showAlert(Alert.AlertType.WARNING, "Sách không sẵn sàng", checkStatus);
                    return; // Sách đang bị mượn -> Lập tức Đuổi về, KHÔNG CHO CHẠY TIẾP XUỐNG DƯỚI
                }

                // BƯỚC 3: Nếu qua được bảo vệ an toàn -> Mới bắt đầu ghi vào Database
                String borrowCode = "PM-" + (System.currentTimeMillis() % 10000);
                Borrow newBorrow = new Borrow(0, borrowCode, "", "", null, dueDateStr, 0, "Borrowing");

                boolean isSuccess = borrowDAO.insertBorrowWithDetailsById(newBorrow, readerId, staffId, copyIds);

                if (isSuccess) {
                    showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã tạo phiếu mượn!");
                    handleClosePopup();
                    loadDataToTable();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Thất bại", "Lỗi tạo phiếu! Có thể CopyID không tồn tại.");
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi cú pháp", "CopyID chỉ được nhập số (VD: 1, 2, 3)!");
            }
        } else {
            // Chế độ Sửa
            showAlert(Alert.AlertType.INFORMATION, "Tính năng", "Sửa phiếu đang bảo trì để nâng cấp!");
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

        borrowTable.setRowFactory(tv -> {
            javafx.scene.control.TableRow<Borrow> row = new javafx.scene.control.TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    showDetailPopup(row.getItem());
                }
            });
            return row;
        });
        detailPopupOverlay.setOnMouseClicked(event -> {
            if (event.getTarget() == detailPopupOverlay) detailPopupOverlay.setVisible(false);
        });

        //lock ngày trả sách trong quá khu và hiện tại
        dpPopupDueDate.setDayCellFactory(picker -> new javafx.scene.control.DateCell() {
            @Override
            public void updateItem(java.time.LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                // Nếu ngày đó nhỏ hơn hoặc bằng ngày hôm nay -> Khóa lại
                if (date != null && !date.isAfter(java.time.LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #eeeeee; -fx-text-fill: #999999;");
                }
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

    // Hàm đẩy dữ liệu lên Pop-up chi tiết
    private void showDetailPopup(Borrow borrow) {
        lblDetailTitle.setText("Chi Tiết: " + borrow.getBorrowCode());
        lblDetailReader.setText(borrow.getReaderCode());
        lblDetailStaff.setText(borrow.getStaffCode());
        lblDetailTime.setText("Từ: " + borrow.getBorrowDate() + "  ->  Đến: " + borrow.getDueDate());

        // Kéo list sách từ Database
        List<String> books = borrowDAO.getBorrowedBooks(borrow.getBorrowId());
        lvDetailBooks.setItems(FXCollections.observableArrayList(books));

        detailPopupOverlay.setVisible(true);
    }

    @FXML
    void handleCloseDetailPopup() {
        detailPopupOverlay.setVisible(false);
    }


}