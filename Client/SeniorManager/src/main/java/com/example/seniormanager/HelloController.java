package com.example.seniormanager;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Đây là form dành cho quản lý cấp cao - senior manager");
    }
}
