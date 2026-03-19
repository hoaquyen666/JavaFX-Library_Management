package com.example.librarian.controller;

import com.example.librarian.dao.BookCopyDAO;
import com.example.librarian.model.BookCopy;
import com.example.librarian.util.MessageBox;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class BookCopyManagementController implements Initializable {

    @FXML private TableView<BookCopy> copyTable;
    @FXML private TableColumn<BookCopy, Integer> colCopyId;
    @FXML private TableColumn<BookCopy, Integer> colBookId;
    @FXML private TableColumn<BookCopy, String> colBookTitle;
    @FXML private TableColumn<BookCopy, String > colStatus;
    @FXML private TableColumn<BookCopy, String > colLocation;
    @FXML private TableColumn<BookCopy, String > colNote;

    @FXML private TextField searchField;
    @FXML private Label lblTotalRecords;
    @FXML private Label lblCurrentPage;
    @FXML private ComboBox<Integer> cbRowsPerPage;

    @FXML private StackPane editPopupOverlay;
    @FXML private Label lblPopupBookInfo;
    @FXML private ComboBox<String> cbPopupStatus;
    @FXML private TextField txtPopupLocation, txtPopupNote;

    private BookCopyDAO copyDAO = new BookCopyDAO();
    private List<BookCopy> allData;
    private List<BookCopy> filteredData;
    private BookCopy editingCopy = null;

    private int currentPage = 1;
    private int rowsPerPage = 20;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colCopyId.setCellValueFactory(new PropertyValueFactory<>("copyId"));
        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        colBookTitle.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colNote.setCellValueFactory(new PropertyValueFactory<>("note"));

        //Css trạng thái
        colStatus.setCellFactory(column -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty){
                super.updateItem(item, empty);
                if(empty || item == null) setGraphic(null);
                else{
                    Label label = new Label(item);
                    String color = switch (item){
                        case "Available" -> "#31b865"; // Xanh lá
                        case "Borrowed" -> "#1a73e8";  // Xanh dương
                        case "Lost" -> "#ed3736";      // Đỏ
                        case "Fix" -> "#f39c12";    // Cam
                        default -> "#b2bec3";
                    };
                    label.setStyle("-fx-text-fill: white; -fx-background-color: " + color + "; -fx-padding: 4 10; -fx-background-radius: 5; -fx-alignment: CENTER; -fx-pref-width: 90;");
                    setGraphic(label);
                }
            }
        } );

        cbPopupStatus.getItems().addAll("Có sẵn", "Đang mượn", "Mất", "Hỏng");

        cbRowsPerPage.getItems().addAll(10,20,50);
        cbRowsPerPage.setValue(20);
        cbRowsPerPage.setOnAction(event -> {
            rowsPerPage = cbRowsPerPage.getValue();
            currentPage = 1;
            updateTable();
        });


        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterData());

        loadData();
    }

    //Load dữ liệu
    private void loadData(){
        allData = copyDAO.getAllBookCopies(); //Lưu tất cả bản copy vào biến allData
        filterData();
    }

    //Hàm lọc dữ liệu khi nhập vào searchField
    private void filterData(){
        if(allData.isEmpty()) return;
        String search = removeAccents(searchField.getText().trim());

        filteredData = new ArrayList<>();
        for(BookCopy bookCopy : allData){ //Duyệt
            String title = removeAccents(bookCopy.getBookTitle());
            if(title.contains(search)){ //Nếu search có trong title, cho vào ds lọc
                filteredData.add(bookCopy);
            }
        }
        currentPage = 1;
        updateTable();
    }

    //Loại bỏ dấu tiếng việt và chuyển về chữ in thường
    private String removeAccents(String text) {
        if (text == null) return "";
        String nfd = Normalizer.normalize(text, Normalizer.Form.NFD);
        return Pattern.compile("\\p{InCombiningDiacriticalMarks}+").matcher(nfd).replaceAll("").toLowerCase();
    }

    //Cập nhật bảng
    private void updateTable(){
        //Xử lý bảng rỗng
        if(filteredData.isEmpty()){
            copyTable.setItems(FXCollections.observableArrayList());
            lblTotalRecords.setText("Tổng 0 bản ghi");
            lblCurrentPage.setText("1");
            return;
        }

        lblTotalRecords.setText("Tổng: " + filteredData.size()+ " bản sao");
        //Cắt trang từ dòng đến dòng
        int fromIndex = (currentPage - 1) * rowsPerPage;
        int toIndex = Math.min(fromIndex + rowsPerPage, filteredData.size());

        if (fromIndex <= toIndex && fromIndex >= 0) {
            //Hiển thị vào bảng
            copyTable.setItems(FXCollections.observableArrayList(filteredData.subList(fromIndex, toIndex)));
        }
        lblCurrentPage.setText(String.valueOf(currentPage));

    }

    @FXML void handlePrevPage() {
        if (currentPage > 1) {
            currentPage--; updateTable();
        }
    }
    @FXML void handleNextPage() {
        if (currentPage < Math.ceil((double) filteredData.size() / rowsPerPage))
        {
            currentPage++; updateTable();
        }
    }

    @FXML
    void handleEditCopy(){
        editingCopy = copyTable.getSelectionModel().getSelectedItem(); // Lấy copy từ dòng b chọn
        if(editingCopy == null) {
            MessageBox.showError("Vui lòng chọn 1 bản sao để sửa ");
        }

        //Không cho sửa nếu trạng thái là đang mượn
        if("Đang mượn".equalsIgnoreCase(editingCopy.getStatus())){
            cbPopupStatus.setDisable(true);
        }else
            cbPopupStatus.setDisable(false);

        lblPopupBookInfo.setText("Sách: "+ editingCopy.getBookTitle() + " ID bản sao: "+ editingCopy.getCopyId());
        cbPopupStatus.setValue(editingCopy.getStatus());
        txtPopupLocation.setText(editingCopy.getLocation() != null ? editingCopy.getLocation() : "");
        txtPopupNote.setText(editingCopy.getNote() != null ? editingCopy.getNote() : "");

        editPopupOverlay.setVisible(true); //Hiện form

    }

    //hàm save cho nút save
    @FXML
    void handleSaveCopy(){
        editingCopy.setStatus(cbPopupStatus.getValue());
        editingCopy.setLocation(txtPopupLocation.getText().trim());
        editingCopy.setNote(txtPopupNote.getText().trim());

        if(copyDAO.updateBookCopy(editingCopy)){
            MessageBox.showInfo("Đã cập nhật thông tin bản sao");
            handleClosePopup();
            loadData();
        }
        else {
            MessageBox.showError("Lỗi cập nhật CSDL");
        }
    }

    @FXML
    void handleClosePopup() {editPopupOverlay.setVisible(false);}


}
