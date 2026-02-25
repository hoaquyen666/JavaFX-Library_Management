package com.example.seniormanager.controller;

import com.example.seniormanager.util.MessageBox;
import com.example.seniormanager.util.SendMail;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;


public class TicketHelpController {
    @FXML
    private TextField txtTitle;

    @FXML
    private TextField txtSeniorCode;

    @FXML
    private TextArea txtDescription;


    @FXML
    protected void onHelpTicketClick()
    {
        String title = txtTitle.getText();
        String seniorcode =  txtSeniorCode.getText();
        String description = txtDescription.getText();

        if (title.isBlank() || seniorcode.isBlank() || description.isBlank())
        {
            MessageBox.showInfo("Vui lòng nhập đầy đủ thông tin");
            return;
        }

        // luồng phụ
        Task<Void> task = new Task<>()
        {
            @Override protected Void call() throws Exception {
                SendMail.sendTicket(title, seniorcode,description);
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            MessageBox.showInfo("Đã gửi ticket thành công!");
            txtTitle.clear();
            txtSeniorCode.clear();
            txtDescription.clear();
        });

        task.setOnFailed(e -> {
            Throwable ex = task.getException();

            MessageBox.showError("Gửi ticket thất bại", ex.getMessage() != null
                    ? ex.getMessage() : "Đã xảy ra lỗi không xác định");

            ex.printStackTrace();
        });

        new Thread(task).run();
    }
}
