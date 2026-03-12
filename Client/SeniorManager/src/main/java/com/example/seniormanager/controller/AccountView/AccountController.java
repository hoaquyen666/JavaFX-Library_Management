package com.example.seniormanager.controller.AccountView;

import com.example.seniormanager.dao.AccountDAO;
import com.example.seniormanager.model.Account;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AccountController implements Initializable {
    @FXML private TableView<Account> accountTable;
    @FXML private TableColumn<Account, Integer> colSTT;
    @FXML private TableColumn<Account, Integer> colStaffId;
    @FXML private TableColumn<Account, String> colStaffName;
    @FXML private TableColumn<Account, String> colUsername;
    @FXML private TableColumn<Account, String> colPassword;

    private AccountDAO accountDAO = new AccountDAO();
    private ObservableList<Account> accountList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadData();

        accountTable.setOnMouseClicked(event -> {
            if(event.getClickCount() == 2 && accountTable.getSelectionModel().getSelectedItem() != null){
                editAccount(null);
            }
        });

    }

    private void setupTableColumns(){
        //Cột stt
        colSTT.setCellValueFactory(column -> new ReadOnlyObjectWrapper<>(accountTable.getItems().indexOf(column.getValue()) + 1));

        //Map các thuộc tính với Account
        colStaffId.setCellValueFactory(new PropertyValueFactory<>("staffId"));
        colStaffName.setCellValueFactory(new PropertyValueFactory<>("staffName"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colPassword.setCellValueFactory(new PropertyValueFactory<>("passwordHash"));
    }

    public void loadData(){
        accountList.clear();
        List<Account> data = accountDAO.getAllStaffAccount();
        accountList.addAll(data);
        accountTable.setItems(accountList);
    }

    @FXML
    void addAccount(ActionEvent event){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/seniormanager/staff-management/account-view/AddAccountForm.fxml"));
            Parent root = loader.load();

            //Mở cửa sổ
            Stage stage = new Stage();
            stage.setTitle("Thêm tài khoản nhân viên");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            // Truyền reference của controller này sang form thêm để tí nữa gọi loadData()
//             AddAccountController addController = loader.getController();
//             addController.setParentController(this);

            stage.showAndWait();

            loadData();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    void editAccount(ActionEvent event){
        Account selectedAccount = accountTable.getSelectionModel().getSelectedItem();

        if(selectedAccount == null){
            // Nếu chưa chọn ai mà đã bấm nút Sửa
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Chưa chọn dữ liệu");
            alert.setHeaderText(null);
            alert.setContentText("Vui lòng chọn một nhân viên trong bảng để sửa!");
            alert.showAndWait();
            return;
        }

        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/seniormanager/staff-management/account-view/EditAccount.fxml"));
            Parent root = loader.load();

            //Lấy controller từ loader load từ view dữ liệu
            EditAccountController controller = loader.getController();
            //Gán account được chọn để gửi đi cho controller
            controller.setAccountData(selectedAccount);

            Stage stage = new Stage();
            stage.setTitle("CHỈNH SỬA TÀI KHOẢN NHÂN VIÊN");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            //Load lại data sau khi sửa
            loadData();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
