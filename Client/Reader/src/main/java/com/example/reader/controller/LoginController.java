package com.example.reader.controller;

import com.example.reader.dao.AccountDAO;
import com.example.reader.model.Account;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;


    private AccountDAO accountDAO = new AccountDAO();

    private static String ROLE = "Reader";

    @FXML
    protected void onLoginClick() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            return;
        }



        Account account = accountDAO.login(username, password, ROLE);

        return;
//        if (account != null) {
//            try {
//                FXMLLoader loader = new FXMLLoader(
//                        getClass().getResource("/com/example/reader/search-view/search-main-view.fxml"));
//                Scene scene = new Scene(loader.load(), 1500, 1000);
//                // scene.getStylesheets().add(getClass().getResource("/style/ButtonStyle.css").toExternalForm());
//                Stage stage = (Stage) txtUsername.getScene().getWindow();
//                stage.setScene(scene);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

    @FXML
    protected void onRegisterClick() {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/reader/register-view.fxml"));
//            Scene scene = new Scene(loader.load(), 400, 500);
//            Stage stage = (Stage) txtUsername.getScene().getWindow();
//            stage.setScene(scene);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return;
    }
}
