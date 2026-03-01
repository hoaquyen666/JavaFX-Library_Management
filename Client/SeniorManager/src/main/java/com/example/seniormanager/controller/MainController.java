package com.example.seniormanager.controller;

import com.example.seniormanager.util.MessageBox;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML
    private Node root; // Bất kỳ node nào trong FXML, dùng để lấy Stage

    @FXML
    protected void onShiftClick() // Chuyển sang tab ca làm
    {

    }

    @FXML
    protected void onReortClick() // Chuyển sang tab báo cáo
    {

    }

    @FXML
    protected void onReturnLoginScreen() // Trở lại màn hình đăng nhập
    {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/seniormanager/login-view.fxml"));
            Scene scene = new Scene(loader.load(), 400, 370);
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            MessageBox.showError("Không thể quay lại màn hình đăng nhập.", e.getMessage());
        }
    }
}
