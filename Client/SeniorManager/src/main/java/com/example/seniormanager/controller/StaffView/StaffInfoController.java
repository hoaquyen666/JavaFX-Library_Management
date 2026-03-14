package com.example.seniormanager.controller.StaffView;

import com.example.seniormanager.dao.StaffInfoDAO;
import com.example.seniormanager.model.StaffInfo;
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
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class StaffInfoController implements Initializable {
    @FXML private TableView<StaffInfo> StaffTable;
    @FXML private TableColumn<StaffInfo, Integer> colSTT;
    @FXML private TableColumn<StaffInfo, Integer> colStaffId;
    @FXML private TableColumn<StaffInfo, String> colStaffName;
    @FXML private TableColumn<StaffInfo, String> colEmail;
    @FXML private TableColumn<StaffInfo, String> colPhone;
    @FXML private TableColumn<StaffInfo, Date> colDob;

    private StaffInfoDAO staffInfoDAO = new StaffInfoDAO();
    private ObservableList<StaffInfo> staffList= FXCollections.observableArrayList();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUpTableColums(); loadData();

        StaffTable.setOnMouseClicked(event -> {
            // Nếu nhấp đúp (2 lần) và đang trỏ vào một dòng có dữ liệu
            if (event.getClickCount() == 2 && StaffTable.getSelectionModel().getSelectedItem() != null) {
                editStaff(null);
            }
        });
    }

    private void setUpTableColums(){
        //Cột stt
        colSTT.setCellValueFactory(column -> new ReadOnlyObjectWrapper<>(StaffTable.getItems().indexOf(column.getValue()) + 1));

        colStaffId.setCellValueFactory(new PropertyValueFactory<>("staffId"));
        colStaffName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        colDob.setCellValueFactory(new PropertyValueFactory<>("DoB"));
    }

    private void loadData(){
        staffList.clear();
        List<StaffInfo> data = StaffInfoDAO.getAllStaff();
        staffList.addAll(data);
        StaffTable.setItems(staffList);
    }

    @FXML
    void addStaff(ActionEvent event) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/seniormanager/staff-management/staff-view/AddStaffForm.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Thêm nhân viên");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            stage.showAndWait();

            loadData();
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    @FXML
    void editStaff(ActionEvent event){
        System.out.println("Thành công");
        StaffInfo selectedStaff = StaffTable.getSelectionModel().getSelectedItem();

        if(selectedStaff == null){
            // Nếu chưa chọn ai mà đã bấm nút Sửa
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Chưa chọn dữ liệu");
            alert.setHeaderText(null);
            alert.setContentText("Vui lòng chọn một nhân viên trong bảng để sửa!");
            alert.showAndWait();
            return;
        }

        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/seniormanager/staff-management/staff-view/EditStaffForm.fxml"));
            Parent root = loader.load();

            EditStaffController controller = loader.getController();
            controller.setStaffData(selectedStaff);

            Stage stage = new Stage();
            stage.setTitle("SỬA NHÂN VIÊN");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            //Sau khi sửa xong load lại data
            loadData();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
