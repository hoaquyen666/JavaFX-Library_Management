package com.example.librarian.controller;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
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

public class MainViewController implements Initializable {

    @FXML
    private StackPane mainContentArea;

    @FXML
    private VBox sidebar;

    @FXML
    private VBox muonTraSubMenu;
    private boolean isMuonTraExpanded = false;

    // Chiều rộng khi thu gọn (chỉ hiện icon)
    private final double COLLAPSED_WIDTH = 70.0;
    // Chiều rộng khi mở rộng (hiện cả text)
    private final double EXPANDED_WIDTH = 220.0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
    void handleQuanLySach(ActionEvent event){
        loadGiaoDienCon("/com/example/librarian/Book_Management/book-management-view.fxml");
    }

    @FXML
    void handleQuanLyDocGia(ActionEvent event){
        loadGiaoDienCon("/com/example/librarian/Reader_Management/reader-management-view.fxml");
    }


    @FXML
    void handleQuanLyMuonTra(ActionEvent event) {
        isMuonTraExpanded = !isMuonTraExpanded;
        muonTraSubMenu.setVisible(isMuonTraExpanded);
        muonTraSubMenu.setManaged(isMuonTraExpanded);
    }

    @FXML
    void handleQuanLyMuon(ActionEvent event){
        loadGiaoDienCon("/com/example/librarian/Borrow_Return_Management/borrow-management-view.fxml");
    }

    @FXML
    void handleQuanLyTra(ActionEvent event){
        loadGiaoDienCon("/com/example/librarian/Borrow_Return_Management/return-management-view.fxml");
    }

    @FXML
    void handleQuanLyNhapXuat(ActionEvent event){
        loadGiaoDienCon("/com/example/librarian/Import_Export_Management/import-export-management-view.fxml");
    }

    @FXML
    void handleQuanLyThongKe(ActionEvent event){
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

        }catch (IOException e){
            System.err.println("Lỗi khi đọc file FXML: " + e.getMessage());
            e.printStackTrace();
        }catch (NullPointerException e){
            System.err.println("Lỗi đường dẫn NullPointerException");
            e.printStackTrace();
        }
    }
}