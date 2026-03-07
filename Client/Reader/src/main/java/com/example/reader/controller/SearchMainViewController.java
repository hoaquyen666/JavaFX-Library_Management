package com.example.reader.controller;

import com.example.reader.dao.BookDAO;
import com.example.reader.model.Book;
import com.example.reader.util.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;     // Import ComboBox của JavaFX
import javafx.scene.control.TextField;    // Import TextField của JavaFX
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class SearchMainViewController {

    @FXML
    private Button btnLogin;

    @FXML
    private Button btnRegister;

    @FXML
    private TextField txtSearch;

    @FXML
    protected void onLoginForm() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/reader/login-view.fxml"));
        Scene scene = new Scene(loader.load(), 400, 370);
        Stage stage = (Stage) btnLogin.getScene().getWindow();
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    @FXML
    protected void onRegisterForm() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/reader/register-view.fxml"));
        Scene scene = new Scene(loader.load(), 400, 500);
        Stage stage = (Stage) btnRegister.getScene().getWindow();
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    @FXML
    private ComboBox<String> searchType;

    private BookDAO bookDAO = new BookDAO();

    @FXML
    public void initialize() {
        if (searchType != null) {
            searchType.getItems().addAll("Tên sách", "Mã sách", "ISBN");
            searchType.getSelectionModel().selectFirst();
        }
    }

    @FXML
    protected void onSearchAction() {
        String keyword = txtSearch.getText().trim();
        String type = searchType.getValue();

        if (keyword.isEmpty()) {
            System.out.println("Vui lòng nhập từ khóa!");
            return;
        }

        // Gọi sang lớp DAO để lấy dữ liệu
        List<Book> results = bookDAO.searchBooks(type, keyword);

        // Hiển thị kết quả
        if (results.isEmpty()) {
            System.out.println("Không tìm thấy kết quả.");
        } else {
            System.out.println("Kết quả tìm kiếm");
            results.forEach(System.out::println);
        }
    }
}
