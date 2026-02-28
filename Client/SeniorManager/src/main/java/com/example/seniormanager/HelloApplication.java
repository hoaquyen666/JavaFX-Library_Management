package com.example.seniormanager;

import com.example.seniormanager.util.DatabaseConnection;
import com.example.seniormanager.util.MessageBox;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        try {

                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication
                        .class.getResource("login-view.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 400, 370);
                stage.setTitle("Đăng nhập");
                stage.setScene(scene);
                stage.show();

        } catch (Exception e) {
            MessageBox.showError("Lỗi [2]", e.getMessage() != null
                    ? e.getMessage() : "Lỗi không xác định [2]");

            e.printStackTrace();
        }
    }
}
