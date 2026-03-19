package com.example.librarian.controller;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainViewController  implements Initializable {

    public static MainViewController instance;

    @FXML
    private StackPane mainContentArea;

    @FXML
    private VBox sidebar;

    @FXML
    private VBox muonTraSubMenu;
    private boolean isMuonTraExpanded = false;

    @FXML
    private VBox menuQuanLyKho;
    private boolean isKhoExpanded = false;

    // Chiều rộng khi thu gọn (chỉ hiện icon)
    private final double COLLAPSED_WIDTH = 70.0;
    // Chiều rộng khi mở rộng (hiện cả text)
    private final double EXPANDED_WIDTH = 220.0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;
        handleTrangChu(null);
        // Đảm bảo sidebar bắt đầu ở trạng thái thu gọn
        if (sidebar != null) {
            sidebar.setPrefWidth(COLLAPSED_WIDTH);

            //Khi chuột di chuyển vào Sidebar
            sidebar.setOnMouseEntered(event -> openSidebar());

            //Khi chuột rời khỏi Sidebar
            sidebar.setOnMouseExited(event -> closeSidebar());
        }
    }

    private void openSidebar() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.2),
                        new KeyValue(sidebar.prefWidthProperty(), EXPANDED_WIDTH))
        );
        timeline.play();
    }

    private void closeSidebar() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.2),
                        new KeyValue(sidebar.prefWidthProperty(), COLLAPSED_WIDTH))
        );
        timeline.play();
    }


    @FXML
    public void handleTrangChu(ActionEvent event) {
        setActive(event);
        loadGiaoDienCon("/com/example/librarian/Library_Main_View/home-main-view.fxml");
    }

    @FXML
    public void handleQuanLySach(ActionEvent event){
        setActive(event);
        loadGiaoDienCon("/com/example/librarian/Book_Management/book-management-view.fxml");
    }

    @FXML
    public void handleQuanLyDocGia(ActionEvent event){
        setActive(event);
        loadGiaoDienCon("/com/example/librarian/Reader_Management/reader-management-view.fxml");
    }


    @FXML
    public void handleQuanLyMuonTra(ActionEvent event) {
        setActive(event);
        isMuonTraExpanded = !isMuonTraExpanded;
        muonTraSubMenu.setVisible(isMuonTraExpanded);
        muonTraSubMenu.setManaged(isMuonTraExpanded);
    }

    @FXML
    public void handleQuanLyMuon(ActionEvent event){
        setActive(event);
        loadGiaoDienCon("/com/example/librarian/Borrow_Return_Management/borrow-management-view.fxml");
    }

    @FXML
    public void handleQuanLyTra(ActionEvent event){
        setActive(event);
        loadGiaoDienCon("/com/example/librarian/Borrow_Return_Management/return-management-view.fxml");
    }

    @FXML
    public void handleQuanLyBanSao(ActionEvent event){
        setActive(event);
        loadGiaoDienCon("/com/example/librarian/Book_Management/bookcopy-management-view.fxml");
    }

//    @FXML
//    public void handleQuanLyKho(ActionEvent event){
//        setActive(event);
//        isKhoExpanded = !isKhoExpanded;
//        menuQuanLyKho.setVisible(isKhoExpanded);
//        menuQuanLyKho.setManaged(isKhoExpanded);
//    }
//
//    @FXML
//    public void handleQuanLyNhap(ActionEvent event){
//        setActive(event);
//        loadGiaoDienCon("/com/example/librarian/Storage_Management/import-management-view.fxml");
//    }
//
//    @FXML
//    public void handleNhaCungCap(ActionEvent event){
//        setActive(event);
//        loadGiaoDienCon("/com/example/librarian/Storage_Management/supplier-management-view.fxml");
//    }

    @FXML
    public void handleQuanLyThongKe(ActionEvent event){
        setActive(event);
        loadGiaoDienCon("/com/example/librarian/Statistical_Management/statistical-management-view.fxml");
    }

    private void loadGiaoDienCon(String fileName){
        try{
            URL resourceUrl = getClass().getResource(fileName);
            if (resourceUrl == null) {
                System.err.println("Lỗi đường dẫn: KHÔNG TÌM THẤY FILE " + fileName);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent subView = loader.load();

            mainContentArea.getChildren().clear();
            mainContentArea.getChildren().add(subView);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(350), subView);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);

            TranslateTransition slideUp = new TranslateTransition(Duration.millis(350), subView);
            slideUp.setFromY(30);
            slideUp.setToY(0);

            ParallelTransition transition = new ParallelTransition(fadeIn, slideUp);
            transition.play();
        }catch (IOException e){
            System.err.println("Lỗi khi đọc file FXML: " + e.getMessage());
            e.printStackTrace();
        }catch (NullPointerException e){
            System.err.println("Lỗi đường dẫn NullPointerException");
            e.printStackTrace();
        }
    }
    // Biến lưu trữ nút đang được click
    private javafx.scene.control.Button currentActiveButton;

    // Hàm 1: Đổi màu nút đang chọn
    private void setActive(ActionEvent event) {
        if (event != null && event.getSource() instanceof javafx.scene.control.Button) {
            javafx.scene.control.Button clickedBtn = (javafx.scene.control.Button) event.getSource();
            // Xóa CSS active ở nút cũ
            if (currentActiveButton != null) {
                currentActiveButton.getStyleClass().remove("active-nav");
            }
            // Gắn CSS active vào nút mới
            if (!clickedBtn.getStyleClass().contains("active-nav")) {
                clickedBtn.getStyleClass().add("active-nav");
            }
            currentActiveButton = clickedBtn;
        }
    }

}