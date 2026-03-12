package com.example.seniormanager;

import com.example.seniormanager.util.MessageBox;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) {

        try {

                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication
                        .class.getResource("/com/example/seniormanager/staff-management/main-view/staff-management-view.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                stage.setTitle("Đăng nhập");
                stage.fullScreenProperty();
                stage.setScene(scene);
                stage.show();

        } catch (Exception e) {
            MessageBox.showError("Lỗi [2]", e.getMessage() != null
                    ? e.getMessage() : "Lỗi không xác định [2]");

            e.printStackTrace();
        }
    }
}
