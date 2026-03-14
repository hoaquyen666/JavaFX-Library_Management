package com.example.reader.controller;

import com.example.reader.model.Book;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class SearchResultController {
    @FXML
    private ListView<Book> resultsListView;

    public void setResults(List<Book> books){
        //Đưa dữ liệu vào
        resultsListView.setItems(FXCollections.observableArrayList(books));

        // Tuỳ chỉnh giao diện
        resultsListView.setCellFactory(param -> new ListCell<Book>() {
            // Khai báo các thành phần UI cho 1 dòng
            private HBox content;
            private ImageView imageView;
            private Label lblTitle;
            private Label lblDetails;

            // Khối khởi tạo: Chạy 1 lần để tạo bố cục (Layout)
            {
                imageView = new ImageView();
                imageView.setFitWidth(80);
                imageView.setFitHeight(110);
                //  tải ảnh bằng: imageView.setImage(new Image("URL hoặc Path"));
                imageView.setStyle("-fx-border-color: gray; -fx-border-width: 1px;");

                lblTitle = new Label();
                lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2c3e50;");

                lblDetails = new Label();
                lblDetails.setWrapText(true); // Cho phép xuống dòng nếu văn bản dài

                // Đóng gói Text vào VBox (bên phải)
                VBox vBox = new VBox(5, lblTitle, lblDetails);

                // Đóng gói Image và VBox vào HBox (Hàng ngang: Ảnh trái, Text phải)
                content = new HBox(15, imageView, vBox);
                content.setStyle("-fx-padding: 10px;");
            }

            @Override
            protected void updateItem(Book book, boolean empty){
                super.updateItem(book, empty);

                if(empty || book == null){
                    setGraphic(null);
                }else {
                    //Gán data vào label
                    lblTitle.setText(book.getTitle());
                    lblDetails.setText(
                            "Tác giả: " + book.getAuthor() + "\n" +
                                    "Thể loại: " + book.getCategory() + " (" + book.getCategoryGroup() + ")\n" +
                                    "NXB: " + book.getPublisher() + " - Năm: " + book.getPublishYear() + "\n" +
                                    "ISBN: " + book.getIsbn()
                    );

                    setGraphic(content);
                }
            }
        });
    }
}
