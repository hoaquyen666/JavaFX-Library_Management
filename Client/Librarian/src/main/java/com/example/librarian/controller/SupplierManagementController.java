package com.example.librarian.controller;

import com.example.librarian.dao.SupplierDAO;
import com.example.librarian.model.Supplier;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class SupplierManagementController {

    @FXML
    private TableView<Supplier> supplierTable;

    @FXML
    private TableColumn<Supplier, Integer> colSelect;

    @FXML
    private TableColumn<Supplier, String> colSupplierCode;

    @FXML
    private TableColumn<Supplier, String> colSupplierName;

    @FXML
    private TableColumn<Supplier, String> colPhone;

    @FXML
    private TableColumn<Supplier, String> colEmail;

    @FXML
    private TableColumn<Supplier, String> colAddress;

    @FXML
    private TableColumn<Supplier, String> colStatus;

    @FXML
    private TextField searchField;

    @FXML
    private Label totalCountLabel;

    @FXML
    private ComboBox<String> statusFilter;

    @FXML
    private ComboBox<String> pageSizeCombo;

    private final ObservableList<Supplier> masterRows = FXCollections.observableArrayList();
    private FilteredList<Supplier> filteredRows;

    private final SupplierDAO supplierDAO = new SupplierDAO();

    @FXML
    private void initialize() {
        setupFilters();
        setupTable();
        loadData();
        bindTable();
        applyFilter();
    }

    private void setupFilters() {
        statusFilter.setItems(FXCollections.observableArrayList(
                "Tất cả trạng thái",
                "hoạt động",
                "ngừng hoạt động"
        ));
        statusFilter.getSelectionModel().selectFirst();
        statusFilter.valueProperty().addListener((obs, oldValue, newValue) -> applyFilter());

        pageSizeCombo.setItems(FXCollections.observableArrayList(
                "20 / trang",
                "50 / trang",
                "100 / trang"
        ));
        pageSizeCombo.getSelectionModel().selectFirst();

        searchField.textProperty().addListener((obs, oldValue, newValue) -> applyFilter());
        searchField.setOnAction(event -> applyFilter());
    }

    private void setupTable() {
        supplierTable.setEditable(false);
        supplierTable.setPlaceholder(new Label("Không có nhà cung cấp"));
        supplierTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        colSelect.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(
                        supplierTable.getItems().indexOf(cellData.getValue()) + 1
                ).asObject());

        colSupplierCode.setCellValueFactory(cellData ->
                new SimpleStringProperty(nullToEmpty(cellData.getValue().getCode())));
        colSupplierName.setCellValueFactory(cellData ->
                new SimpleStringProperty(nullToEmpty(cellData.getValue().getName())));
        colPhone.setCellValueFactory(cellData ->
                new SimpleStringProperty(nullToEmpty(cellData.getValue().getPhone())));
        colEmail.setCellValueFactory(cellData ->
                new SimpleStringProperty(nullToEmpty(cellData.getValue().getEmail())));
        colAddress.setCellValueFactory(cellData ->
                new SimpleStringProperty(nullToEmpty(cellData.getValue().getAddress())));
        colStatus.setCellValueFactory(cellData ->
                new SimpleStringProperty(nullToEmpty(cellData.getValue().getStatus())));
    }

    private void loadData() {
        List<Supplier> suppliers = supplierDAO.getAllSuppliers();
        masterRows.setAll(suppliers);
    }

    private void bindTable() {
        filteredRows = new FilteredList<>(masterRows, supplier -> true);
        SortedList<Supplier> sortedRows = new SortedList<>(filteredRows);
        sortedRows.comparatorProperty().bind(supplierTable.comparatorProperty());
        supplierTable.setItems(sortedRows);
    }

    @FXML
    private void onSearchAction() {
        applyFilter();
    }

    private void applyFilter() {
        if (filteredRows == null) {
            return;
        }

        String keyword = normalize(searchField.getText());
        String selectedStatus = statusFilter.getValue();

        filteredRows.setPredicate(supplier -> {
            boolean keywordMatched = keyword.isBlank()
                    || normalize(supplier.getCode()).contains(keyword)
                    || normalize(supplier.getName()).contains(keyword)
                    || normalize(supplier.getPhone()).contains(keyword)
                    || normalize(supplier.getEmail()).contains(keyword)
                    || normalize(supplier.getAddress()).contains(keyword);

            boolean statusMatched = selectedStatus == null
                    || "Tất cả trạng thái".equals(selectedStatus)
                    || normalize(selectedStatus).equals(normalize(supplier.getStatus()));

            return keywordMatched && statusMatched;
        });

        totalCountLabel.setText(filteredRows.size() + " nhà cung cấp");
        supplierTable.refresh();
    }

    @FXML
    private void handleAddSupplier() {
        Supplier newSupplier = showSupplierDialog(null);
        if (newSupplier == null) {
            return;
        }

        if (supplierDAO.existsByCode(newSupplier.getCode(), null)) {
            showError("Mã nhà cung cấp đã tồn tại.");
            return;
        }

        boolean inserted = supplierDAO.insertSupplier(newSupplier);
        if (!inserted) {
            showError("Không thể thêm nhà cung cấp.");
            return;
        }

        loadData();
        applyFilter();
        showInfo("Thêm nhà cung cấp thành công.");
    }

    @FXML
    private void handleEditSupplier() {
        Supplier selected = supplierTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Vui lòng chọn một nhà cung cấp để sửa.");
            return;
        }

        Supplier editedSupplier = showSupplierDialog(copyOf(selected));
        if (editedSupplier == null) {
            return;
        }

        editedSupplier.setId(selected.getId());

        if (supplierDAO.existsByCode(editedSupplier.getCode(), selected.getId())) {
            showError("Mã nhà cung cấp đã tồn tại.");
            return;
        }

        boolean updated = supplierDAO.updateSupplier(editedSupplier);
        if (!updated) {
            showError("Không thể cập nhật nhà cung cấp.");
            return;
        }

        loadData();
        applyFilter();
        showInfo("Cập nhật nhà cung cấp thành công.");
    }

    @FXML
    private void handleDeleteSupplier() {
        Supplier selected = supplierTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Vui lòng chọn một nhà cung cấp để xóa.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa");
        confirm.setHeaderText("Xóa nhà cung cấp");
        confirm.setContentText("Bạn có chắc muốn xóa nhà cung cấp \"" + nullToEmpty(selected.getName()) + "\" không?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        boolean deleted = supplierDAO.deleteSupplier(selected.getId());
        if (!deleted) {
            showError("Không thể xóa nhà cung cấp.");
            return;
        }

        loadData();
        applyFilter();
        showInfo("Xóa nhà cung cấp thành công.");
    }

    private Supplier showSupplierDialog(Supplier supplier) {
        boolean editing = supplier != null;

        Dialog<Supplier> dialog = new Dialog<>();
        dialog.setTitle(editing ? "Sửa nhà cung cấp" : "Thêm nhà cung cấp");
        dialog.setHeaderText(editing ? "Cập nhật thông tin nhà cung cấp" : "Nhập thông tin nhà cung cấp mới");

        ButtonType saveButtonType = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        TextField codeField = new TextField();
        TextField nameField = new TextField();
        TextField phoneField = new TextField();
        TextField emailField = new TextField();
        TextField addressField = new TextField();
        ComboBox<String> statusBox = new ComboBox<>(FXCollections.observableArrayList("Hoạt động", "Ngừng hoạt động"));

        codeField.setPromptText("Mã NCC");
        nameField.setPromptText("Tên nhà cung cấp");
        phoneField.setPromptText("Số điện thoại");
        emailField.setPromptText("Email");
        addressField.setPromptText("Địa chỉ");
        statusBox.getSelectionModel().select("Hoạt động");

        if (editing) {
            codeField.setText(nullToEmpty(supplier.getCode()));
            nameField.setText(nullToEmpty(supplier.getName()));
            phoneField.setText(nullToEmpty(supplier.getPhone()));
            emailField.setText(nullToEmpty(supplier.getEmail()));
            addressField.setText(nullToEmpty(supplier.getAddress()));
            statusBox.getSelectionModel().select(
                    supplier.getStatus() == null || supplier.getStatus().isBlank() ? "Hoạt động" : supplier.getStatus()
            );
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        grid.add(new Label("Mã NCC:"), 0, 0);
        grid.add(codeField, 1, 0);
        grid.add(new Label("Tên NCC:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Điện thoại:"), 0, 2);
        grid.add(phoneField, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(emailField, 1, 3);
        grid.add(new Label("Địa chỉ:"), 0, 4);
        grid.add(addressField, 1, 4);
        grid.add(new Label("Trạng thái:"), 0, 5);
        grid.add(statusBox, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(buttonType -> {
            if (buttonType != saveButtonType) {
                return null;
            }

            String code = safeTrim(codeField.getText());
            String name = safeTrim(nameField.getText());
            String phone = safeTrim(phoneField.getText());
            String email = safeTrim(emailField.getText());
            String address = safeTrim(addressField.getText());
            String status = statusBox.getValue();

            if (code.isBlank() || name.isBlank()) {
                showError("Mã nhà cung cấp và tên nhà cung cấp không được để trống.");
                return null;
            }

            Supplier result = new Supplier();
            result.setCode(code);
            result.setName(name);
            result.setPhone(phone);
            result.setEmail(email);
            result.setAddress(address);
            result.setStatus(status == null ? "Hoạt động" : status);
            return result;
        });

        Optional<Supplier> result = dialog.showAndWait();
        return result.orElse(null);
    }

    private Supplier copyOf(Supplier supplier) {
        Supplier copy = new Supplier();
        copy.setId(supplier.getId());
        copy.setCode(supplier.getCode());
        copy.setName(supplier.getName());
        copy.setPhone(supplier.getPhone());
        copy.setEmail(supplier.getEmail());
        copy.setAddress(supplier.getAddress());
        copy.setStatus(supplier.getStatus());
        return copy;
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Có lỗi xảy ra");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static String normalize(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT).trim();
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private static String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }
}