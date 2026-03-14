package com.example.librarian.controller;

import com.example.librarian.dao.BookDAO;
import com.example.librarian.model.Book;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import java.text.Normalizer;
import java.util.regex.Pattern;
import java.util.ArrayList;

public class BookManagementController implements Initializable {

    // Khai báo bảng
    @FXML
    private TableView<Book> bookTable;

    // Khai báo các cột
    @FXML
    private TableColumn<Book, Integer> colBookID;
    @FXML
    private TableColumn<Book, String> colBookCode;
    @FXML
    private TableColumn<Book, String> colTitle;
    @FXML
    private TableColumn<Book, String> colISBN;
    @FXML
    private TableColumn<Book, String> colPublisher;
    @FXML
    private TableColumn<Book, Integer> colPublishYear;

    public static boolean showAddPopupOnLoad = false;

    @FXML private StackPane addPopupOverlay;
    @FXML private TextField txtPopupBookCode;
    @FXML private TextField txtPopupTitle;
    @FXML private TextField txtPopupIsbn;
    @FXML private TextField txtPopupPublisher;
    @FXML private TextField txtPopupPublishYear;
    @FXML private Label lblPopupTitle;

    @FXML private Label lblTotalRecords;
    @FXML private Label lblCurrentPage;
    @FXML private ComboBox<Integer> cbRowsPerPage;

    @FXML private TextField searchField; // Ô tìm kiếm
    private List<Book> filteredBooks; // Danh sách đã được lọc

    @FXML
    void handleAddBook() {
        editingBook = null; // Reset về null để hệ thống biết đây là Thêm mới sách

        lblPopupTitle.setText("Thêm sách mới");
        txtPopupBookCode.clear();
        txtPopupTitle.clear();
        txtPopupIsbn.clear();
        txtPopupPublisher.clear();
        txtPopupPublishYear.clear();
        addPopupOverlay.setVisible(true);
    }
    @FXML
    void handleClosePopup() {
        // Xóa trắng các ô nhập liệu
        txtPopupBookCode.clear();
        txtPopupTitle.clear();
        txtPopupIsbn.clear();
        txtPopupPublisher.clear();
        txtPopupPublishYear.clear();

        // Ẩn lớp mờ đi
        addPopupOverlay.setVisible(false);
    }

    // Hàm Lưu sách từ Pop-up vào Database
    @FXML
    void handleSaveNewBook() {
        // 1. Lấy dữ liệu từ các ô nhập liệu
        String code = txtPopupBookCode.getText().trim();
        String title = txtPopupTitle.getText().trim();
        String isbn = txtPopupIsbn.getText().trim();
        String publisher = txtPopupPublisher.getText().trim();
        String yearStr = txtPopupPublishYear.getText().trim();

        // 2. Kiểm tra xem người dùng có bỏ trống không
        if (code.isEmpty() || title.isEmpty() || isbn.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập đầy đủ Mã sách, Tên sách và ISBN!");
            return;
        }

        // 3. Xử lý năm xuất bản (Phải là số)
        int publishYear = 0;
        if (!yearStr.isEmpty()) {
            try {
                publishYear = Integer.parseInt(yearStr);
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi nhập liệu", "Năm xuất bản phải là một con số hợp lệ!");
                return;
            }
        }

        if (editingBook == null) {
            // ---> CHẾ ĐỘ THÊM MỚI
            Book newBook = new Book(0, code, title, isbn, publisher, publishYear);
            if (bookDAO.insertBook(newBook)) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã thêm sách mới!");
                handleClosePopup();
                loadDataToTable();
            } else {
                showAlert(Alert.AlertType.ERROR, "Thất bại", "Không thể thêm sách. Mã sách/ISBN có thể bị trùng!");
            }
        } else {
            // ---> CHẾ ĐỘ SỬA SÁCH
            // Cập nhật thông tin mới vào object đang sửa
            editingBook.setBookCode(code);
            editingBook.setTitle(title);
            editingBook.setISBN(isbn);
            editingBook.setPublisher(publisher);
            editingBook.setPublishYear(publishYear);

            if (bookDAO.updateBook(editingBook)) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã cập nhật thông tin sách!");
                handleClosePopup();
                loadDataToTable();
                bookTable.refresh(); // Ép bảng vẽ lại giao diện cho chắc ăn
            } else {
                showAlert(Alert.AlertType.ERROR, "Thất bại", "Không thể cập nhật thông tin sách!");
            }
        }
    }

    @FXML
    void handleEditBook() {
        // 1. Kiểm tra xem người dùng đã bấm chọn dòng nào trong bảng chưa
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();

        if (selectedBook == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn một cuốn sách trong bảng để sửa!");
            return;
        }

        // 2. Lưu cuốn sách này vào biến trạng thái
        editingBook = selectedBook;
        lblPopupTitle.setText("Sửa thông tin sách");
        // 3. update data của sách vào table
        txtPopupBookCode.setText(selectedBook.getBookCode());
        txtPopupTitle.setText(selectedBook.getTitle());
        txtPopupIsbn.setText(selectedBook.getISBN());
        txtPopupPublisher.setText(selectedBook.getPublisher() != null ? selectedBook.getPublisher() : "");
        txtPopupPublishYear.setText(selectedBook.getPublishYear() > 0 ? String.valueOf(selectedBook.getPublishYear()) : "");

        // 4. Bật Pop-up lên
        addPopupOverlay.setVisible(true);
    }

    @FXML
    void handleDeleteBook() {
        // 1. Lấy cuốn sách đang được bôi xanh trên bảng
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();

        if (selectedBook == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn một cuốn sách trong bảng để xóa!");
            return;
        }

        // 2. Hiện bảng hỏi xác nhận cho chắc cú
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận xóa");
        confirmAlert.setHeaderText("Bạn có chắc chắn muốn xóa sách: " + selectedBook.getTitle() + "?");
        confirmAlert.setContentText("Hành động này không thể hoàn tác.");

        // Nếu bấm OK thì tiến hành xóa
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                boolean isDeleted = bookDAO.deleteBook(selectedBook.getBookID());

                if (isDeleted) {
                    showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã xóa sách khỏi cơ sở dữ liệu!");
                    loadDataToTable(); // Hàm cũ của bạn: Load lại bảng cho mất dòng vừa xóa
                } else {
                    showAlert(Alert.AlertType.ERROR, "Lỗi xóa sách", "Không thể xóa! Sách này có thể đang tồn tại bản sao vật lý hoặc nằm trong phiếu mượn.");
                }
            }
        });
    }

    private BookDAO bookDAO = new BookDAO();
    private Book editingBook = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Cấu hình các cột
        colBookID.setCellValueFactory(new PropertyValueFactory<>("BookID"));
        colBookCode.setCellValueFactory(new PropertyValueFactory<>("BookCode"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("Title"));
        colISBN.setCellValueFactory(new PropertyValueFactory<>("ISBN"));
        colPublisher.setCellValueFactory(new PropertyValueFactory<>("Publisher"));
        colPublishYear.setCellValueFactory(new PropertyValueFactory<>("PublishYear"));

        // 2. Load dữ liệu từ Database lên bảng
        loadDataToTable();
        addPopupOverlay.setVisible(false);
        if (showAddPopupOnLoad == true) {
            addPopupOverlay.setVisible(true);
            showAddPopupOnLoad = false;
        }

        //Phân trang
        // Setup ComboBox chọn số dòng
        cbRowsPerPage.getItems().addAll(10, 20, 50, 100);
        cbRowsPerPage.setValue(20);

        // Sự kiện khi người dùng đổi số dòng/trang (Ví dụ đổi từ 20 sang 50)
        cbRowsPerPage.setOnAction(event -> {
            rowsPerPage = cbRowsPerPage.getValue();
            currentPage = 1; // Tự động quay về trang 1
            updateTablePagination(); // Cập nhật lại bảng
        });

        // Bắt sự kiện gõ phím (Live Search)
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                filteredBooks = new ArrayList<>(allBooksFromDB);
            } else {
                String searchStr = removeAccents(newValue.trim());
                filteredBooks = new ArrayList<>();

                for (Book book : allBooksFromDB) {
                    String titleStr = removeAccents(book.getTitle());
                    String codeStr = removeAccents(book.getBookCode());

                    if (titleStr.contains(searchStr) || codeStr.contains(searchStr)) {
                        filteredBooks.add(book);
                    }
                }
            }
            currentPage = 1;
            updateTablePagination();
        });

        //event click chuột
        bookTable.setOnMouseClicked(event -> {
            // Kiểm tra: Chuột trái click 2 lần VÀ phải click trúng một dòng có dữ liệu
            if (event.getClickCount() == 2 && bookTable.getSelectionModel().getSelectedItem() != null) {

                Book selectedBook = bookTable.getSelectionModel().getSelectedItem();

                // Hiện bảng hỏi xác nhận
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Thêm bản sao sách");
                confirm.setHeaderText("Tạo thêm 1 cuốn vật lý cho sách: " + selectedBook.getTitle() + "?");
                confirm.setContentText("Hệ thống sẽ tự động tạo mã Barcode và đặt trạng thái là Sẵn sàng (Available).");

                // Bắt hành động bấm nút OK
                confirm.showAndWait().ifPresent(response -> {
                    if (response == javafx.scene.control.ButtonType.OK) {
                        boolean success = bookDAO.insertBookCopy(selectedBook.getBookID());
                        if (success) {
                            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã thêm 1 cuốn sách này vào kho!");
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể thêm bản sao sách. Vui lòng thử lại!");
                        }
                    }
                });
            }
        });
    }

    // Hàm làm nhiệm vụ kéo data từ DB lên và lưu vào biến tổng
    private void loadDataToTable() {
        allBooksFromDB = bookDAO.getAllBooks();
        lblTotalRecords.setText(allBooksFromDB.size() + " bản ghi");
        filteredBooks = new ArrayList<>(allBooksFromDB);

        currentPage = 1;
        // Gọi hàm cắt lát để hiển thị
        updateTablePagination();
    }

    // Hàm cắt lát dữ liệu hiển thị lên bảng
    private void updateTablePagination() {
        if (filteredBooks == null || filteredBooks.isEmpty()) {
            bookTable.setItems(FXCollections.observableArrayList());
            lblTotalRecords.setText("Tổng: 0 bản ghi");
            return;
        }

        // Cập nhật nhãn tổng số bản ghi (dựa trên list ĐÃ LỌC)
        lblTotalRecords.setText("Tổng: " + filteredBooks.size() + " bản ghi");

        // Tính toán vị trí cắt trên filteredBooks
        int fromIndex = (currentPage - 1) * rowsPerPage;
        int toIndex = Math.min(fromIndex + rowsPerPage, filteredBooks.size());

        if (fromIndex <= toIndex && fromIndex >= 0) {
            List<Book> pageData = filteredBooks.subList(fromIndex, toIndex);
            bookTable.setItems(FXCollections.observableArrayList(pageData));
        }

        lblCurrentPage.setText(String.valueOf(currentPage));
    }

    // Sự kiện ấn nút lùi
    @FXML
    void handlePrevPage() {
        if (currentPage > 1) {
            currentPage--;
            updateTablePagination();
        }
    }

    // Sự kiện ấn nút tiến
    @FXML
    void handleNextPage() {
        // Tính tổng số trang tối đa
        int maxPage = (int) Math.ceil((double) allBooksFromDB.size() / rowsPerPage);

        if (currentPage < maxPage) {
            currentPage++;
            updateTablePagination();
        }
    }
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Hàm bỏ dấu tiếng Việt và đưa về chữ in thường
    private String removeAccents(String text) {
        if (text == null) return "";
        String nfdNormalizedString = Normalizer.normalize(text, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("")
                .replace("Đ", "D").replace("đ", "d")
                .toLowerCase();
    }
    private List<Book> allBooksFromDB;
    private int currentPage = 1;
    private int rowsPerPage = 20;
}
