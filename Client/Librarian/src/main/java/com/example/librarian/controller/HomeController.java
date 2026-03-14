package com.example.librarian.controller;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

public class HomeController {

    // Sự kiện khi bấm thẻ "Thêm Sách"
    @FXML
    void openThemSach(MouseEvent event) {
        if (MainViewController.instance != null) {
            BookManagementController.showAddPopupOnLoad = true;
            MainViewController.instance.handleQuanLySach(null);
        }
    }

    // Sự kiện khi bấm thẻ "Tạo Phiếu Mượn"
    @FXML
    void openTaoPhieu(MouseEvent event) {
        if (MainViewController.instance != null) {
            MainViewController.instance.handleQuanLyMuon(null);
        }
    }

    // Sự kiện khi bấm thẻ "Xem Thống Kê"
    @FXML
    void openThongKe(MouseEvent event) {
        if (MainViewController.instance != null) {
            MainViewController.instance.handleQuanLyThongKe(null);
        }
    }

}