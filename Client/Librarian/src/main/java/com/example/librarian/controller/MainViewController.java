package com.example.librarian.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainViewController {
    @FXML
    private StackPane mainContentArea;


    @FXML
    void handleQuanLySach(ActionEvent event){
        loadGiaoDienCon("/com/example/librarian/book-management-view.fxml");
    }
    @FXML
    void handleQuanlyDocGia(ActionEvent event){
        loadGiaoDienCon("/com/example/librarian/reader-management-view.fxml");
    }
    
    @FXML
    void handleQuanLyMuon(ActionEvent event){
        loadGiaoDienCon("/com/example/librarian/borrow-management-view.fxml");
    }

    @FXML
    void handleQuanLyTra(ActionEvent event){
        loadGiaoDienCon("/com/example/librarian/return-management-view.fxml");
    }
    @FXML
    void handleQuanLyNhapXuat(ActionEvent event){
        loadGiaoDienCon("/com/example/librarian/import-export-management-view.fxml");
    }
    @FXML
    void handleQuanLyThongKe(ActionEvent event){
        loadGiaoDienCon("/com/example/librarian/statistical-management-view.fxml");
    }
    

    
    private void loadGiaoDienCon(String fileName){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fileName));
            Parent subView = loader.load();

            mainContentArea.getChildren().clear();

            mainContentArea.getChildren().add(subView);
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("Lỗi k tìm thấy file");
        }catch (NullPointerException e){
            e.printStackTrace();
            System.out.println("Lỗi đường dẫn");
        }
    }
}
