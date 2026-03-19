package com.example.librarian.controller;

import com.example.librarian.dao.BorrowDAO;
import com.example.librarian.dao.ReturnDAO;
import com.example.librarian.model.Borrow;
import com.example.librarian.model.ReturnDetailDTO;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class ReturnManagementController implements Initializable {

    // Các thành phần bảng
    @FXML private TableView<ReturnDetailDTO> returnTable;
    @FXML private TableColumn<ReturnDetailDTO, Integer> colBorrowId;
    @FXML private TableColumn<ReturnDetailDTO, Integer> colReaderId;
    @FXML private TableColumn<ReturnDetailDTO, String> colReaderName;
    @FXML private TableColumn<ReturnDetailDTO, String> colCopyId;
    @FXML private TableColumn<ReturnDetailDTO, String> colBookName;
    @FXML private TableColumn<ReturnDetailDTO, Double> colDeposit;
    @FXML private TableColumn<ReturnDetailDTO, Double> colFine;
    @FXML private TableColumn<ReturnDetailDTO, String> colStatus;

    @FXML private TextField txtSearch;
    @FXML private Label lblTotalReturns;
    private ObservableList<ReturnDetailDTO> masterData = FXCollections.observableArrayList();


    private ReturnDAO returnDAO = new ReturnDAO();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadData();
    }

    private void setupTableColumns() {
        colBorrowId.setCellValueFactory(new PropertyValueFactory<>("borrowId"));
        colReaderId.setCellValueFactory(new PropertyValueFactory<>("readerId"));
        colReaderName.setCellValueFactory(new PropertyValueFactory<>("readerName"));
        colCopyId.setCellValueFactory(new PropertyValueFactory<>("displayCopy")); // Lấy mã gộp
        colBookName.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colStatus.setCellFactory(column -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) setGraphic(null);
                else {
                    Label lbl = new Label(item.equals("Borrowing") ? "Đang mượn" : (item.equals("Overdue") ? "Quá hạn" : "Đã trả"));
                    String color = item.equals("Borrowing") ? "#31b865" : (item.equals("Overdue") ? "#ed3736" : "#b2bec3");
                    lbl.setStyle("-fx-text-fill: white; -fx-background-color: " + color + "; -fx-padding: 4 10; -fx-background-radius: 5; -fx-pref-width: 90; -fx-alignment: CENTER;");
                    setGraphic(lbl);
                }
            }
        });

        DecimalFormat df = new DecimalFormat("#,### đ");
        colDeposit.setCellFactory(tc -> new TableCell<>() {
            @Override protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                setText((empty || price == null) ? null : df.format(price));
            }
        });
        colFine.setCellFactory(tc -> new TableCell<>() {
            @Override protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) setText(null);
                else {
                    setText(df.format(price));
                    setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                }
            }
        });


    }

    void loadData() {
        List<ReturnDetailDTO> data = returnDAO.getPendingReturns();
        masterData.setAll(data);
        returnTable.setItems(masterData);
        lblTotalReturns.setText(data.size() + " cuốn");;
    }

    @FXML
    void handleReturnAction() {
        ReturnDetailDTO selectedItem = returnTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            new Alert(Alert.AlertType.WARNING, "Vui lòng chọn một cuốn sách để trả!").show();
            return;
        }

        try {
            //Gọi file fxml popUp
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/librarian/Borrow_Return_Management/payment-popup.fxml"));
            Parent root = loader.load();

            PaymentPopupController paymentPopupController = loader.getController();
            paymentPopupController.setReturnData(selectedItem, this);

            Stage stage = new Stage();
            stage.setTitle("Thanh toán Trả sách");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Lỗi không mở được Popup Thanh toán!");
        }
    }




}