package com.example.reader.util;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ShowError {
    @FXML
    private Label lblError;

    public void showError(String message) {
        lblError.setText(message);
        lblError.setVisible(true);
        lblError.setManaged(true);
    }
}
