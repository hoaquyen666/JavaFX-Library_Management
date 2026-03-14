package com.example.librarian;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class Launcher {
    public static void main(String[] args) {
        Application.launch(HelloApplication.class, args);
    }

    public static class HelloController {
        @FXML
        private Label welcomeText;

        @FXML
        protected void onHelloButtonClick() {
            welcomeText.setText("Đây là form dành cho thủ thư - librarian");
        }
    }
}
