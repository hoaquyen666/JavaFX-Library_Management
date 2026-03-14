package com.example.librarian.controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import java.util.Locale;
import com.example.librarian.dao.ReaderDAO;
import com.example.librarian.model.ReaderRecord;
import javafx.scene.control.TextInputDialog;
import java.util.Optional;
import java.util.List;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import java.util.Optional;

public class ReaderManagementController {
    @FXML
    private ComboBox<String> groupFilter;
    @FXML
    private TextField searchField;
    @FXML
    private TableView<ReaderRow> readerTable;
    @FXML
    private TableColumn<ReaderRow, Boolean> colSelect;
    @FXML
    private TableColumn<ReaderRow, String> colAvatar;
    @FXML
    private TableColumn<ReaderRow, String> colAccount;
    @FXML
    private TableColumn<ReaderRow, String> colFullName;
    @FXML
    private TableColumn<ReaderRow, String> colPhone;
    @FXML
    private TableColumn<ReaderRow, String> colEmail;
    @FXML
    private TableColumn<ReaderRow, String> colDepartment;
    @FXML
    private TableColumn<ReaderRow, String> colAddress;
    @FXML
    private TableColumn<ReaderRow, String> colStatus;
    @FXML
    private Label totalCountLabel;
    @FXML
    private ComboBox<String> pageSizeCombo;
    @FXML
    private Button btnAdd;

    @FXML
    private Button btnEdit;

    @FXML
    private Button btnResetPassword;

    private final ObservableList<ReaderRow> masterRows = FXCollections.observableArrayList();
    private FilteredList<ReaderRow> filteredRows;

    @FXML
    private void initialize() {
        setupFilters();
        setupTable();
        bindTableData();
        loadReadersFromDatabase();
        applyFilter();
    }

    @FXML
    private void onSearchAction() {
        applyFilter();
    }

    private void setupFilters() {
        groupFilter.setItems(FXCollections.observableArrayList(
                "Tất cả nhóm quyền", "Sinh viên", "Giảng viên", "Khách"
        ));
        groupFilter.getSelectionModel().selectFirst();


        pageSizeCombo.setItems(FXCollections.observableArrayList("20 / trang", "50 / trang", "100 / trang"));
        pageSizeCombo.getSelectionModel().selectFirst();

        groupFilter.valueProperty().addListener((obs, oldValue, newValue) -> applyFilter());

        searchField.textProperty().addListener((obs, oldValue, newValue) -> applyFilter());
    }

    private void setupTable() {
        readerTable.setEditable(true);
        readerTable.setPlaceholder(new Label(""));
        readerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        readerTable.setFixedCellSize(40);

        colSelect.setCellValueFactory(cell -> cell.getValue().selectedProperty());
        colSelect.setCellFactory(CheckBoxTableCell.forTableColumn(colSelect));
        colSelect.setSortable(false);
        colSelect.setEditable(true);
        colSelect.setReorderable(false);

        colAvatar.setCellValueFactory(new PropertyValueFactory<>("avatarText"));
        colAvatar.setCellFactory(column -> new TableCell<>() {
            private final Label avatar = new Label();
            private final StackPane wrapper = new StackPane(avatar);

            {
                wrapper.getStyleClass().add("avatar-wrapper");
                avatar.getStyleClass().add("avatar-text");
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    avatar.setText(item);
                    setGraphic(wrapper);
                }
            }
        });
        colAvatar.setSortable(false);
        colAvatar.setReorderable(false);

        colAccount.setCellValueFactory(new PropertyValueFactory<>("account"));
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));

        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }
                Label chip = new Label(item);
                chip.getStyleClass().addAll("status-chip", "status-active");
                setGraphic(chip);
                setText(null);
            }
        });
    }


    private void bindTableData() {
        filteredRows = new FilteredList<>(masterRows, row -> true);
        SortedList<ReaderRow> sortedRows = new SortedList<>(filteredRows);
        sortedRows.comparatorProperty().bind(readerTable.comparatorProperty());
        readerTable.setItems(sortedRows);
    }

    private void loadReadersFromDatabase() {

        ReaderDAO dao = new ReaderDAO();
        List<ReaderRecord> readers = dao.findAllReaders();

        masterRows.clear();

        for (ReaderRecord r : readers) {

            masterRows.add(new ReaderRow(
                    r.getReaderCode(),
                    r.getUsername(),
                    r.getFullName(),
                    r.getPhone(),
                    r.getEmail(),
                    "",
                    "",
                    r.getStatus()
            ));
        }
    }

    private void applyFilter() {
        if (filteredRows == null) {
            return;
        }

        String keyword = normalize(searchField.getText());
        String group = groupFilter.getValue();


        filteredRows.setPredicate(row -> {
            boolean groupMatched = group == null
                    || "Tất cả nhóm quyền".equals(group)
                    || group.equals(row.getGroup());
            boolean keywordMatched = keyword.isBlank()
                    || normalize(row.getAccount()).contains(keyword)
                    || normalize(row.getFullName()).contains(keyword)
                    || normalize(row.getPhone()).contains(keyword)
                    || normalize(row.getEmail()).contains(keyword)
                    || normalize(row.getDepartment()).contains(keyword)
                    || normalize(row.getAddress()).contains(keyword);

            return groupMatched && keywordMatched;
        });

        totalCountLabel.setText(filteredRows.size() + " bản ghi");
    }

    private static String normalize(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT).trim();
    }

    @FXML
    private void onAddReader(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Thêm độc giả");

        TextField txtName = new TextField();
        TextField txtPhone = new TextField();
        TextField txtEmail = new TextField();

        ComboBox<String> cbStatus = new ComboBox<>();
        cbStatus.getItems().addAll("Kích hoạt", "Khóa");
        cbStatus.getSelectionModel().selectFirst();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Họ tên:"),0,0);
        grid.add(txtName,1,0);

        grid.add(new Label("Điện thoại:"),0,1);
        grid.add(txtPhone,1,1);

        grid.add(new Label("Email:"),0,2);
        grid.add(txtEmail,1,2);

        grid.add(new Label("Trạng thái:"),0,3);
        grid.add(cbStatus,1,3);

        dialog.getDialogPane().setContent(grid);

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK,ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();

        if(result.isPresent() && result.get() == ButtonType.OK){

            ReaderRecord reader = new ReaderRecord(
                    "SV" + System.currentTimeMillis(),
                    "",
                    txtName.getText(),
                    txtPhone.getText(),
                    txtEmail.getText(),
                    cbStatus.getValue()
            );

            ReaderDAO dao = new ReaderDAO();
            dao.addReader(reader);

            loadReadersFromDatabase();
        }
    }

    @FXML
    private void onEditReader(){
        ReaderRow selected = readerTable.getSelectionModel().getSelectedItem();

        if(selected == null) return;

        TextInputDialog dialog = new TextInputDialog(selected.getFullName());
        dialog.setTitle("Sửa độc giả");
        dialog.setHeaderText("Sửa họ tên");

        Optional<String> result = dialog.showAndWait();

        if(result.isPresent()){

            String newName = result.get();

            ReaderRecord reader = new ReaderRecord(
                    selected.getAccount(),
                    selected.getAccount(),
                    newName,
                    selected.getPhone(),
                    selected.getEmail(),
                    selected.getStatus()
            );

            ReaderDAO dao = new ReaderDAO();
            dao.updateReader(reader);

            loadReadersFromDatabase();
        }
    }

    @FXML
    private void onResetPassword(){
        ReaderRow selected = readerTable.getSelectionModel().getSelectedItem();
        if(selected == null) return;

        System.out.println("Reset password: " + selected.getAccount());
    }


    public static class ReaderRow {
        private final BooleanProperty selected = new SimpleBooleanProperty(false);
        private final StringProperty account = new SimpleStringProperty();
        private final StringProperty fullName = new SimpleStringProperty();
        private final StringProperty phone = new SimpleStringProperty();
        private final StringProperty email = new SimpleStringProperty();
        private final StringProperty department = new SimpleStringProperty();
        private final StringProperty address = new SimpleStringProperty();
        private final StringProperty status = new SimpleStringProperty();
        private final StringProperty avatarText = new SimpleStringProperty();
        private final StringProperty group = new SimpleStringProperty();

        public ReaderRow(String id, String account, String fullName, String phone, String email,
                         String department, String address, String status) {
            this.account.set(account);
            this.fullName.set(fullName);
            this.phone.set(phone);
            this.email.set(email);
            this.department.set(department);
            this.address.set(address);
            this.status.set(status);
            this.avatarText.set(buildAvatar(fullName));
            this.group.set(resolveGroup(id));
        }

        private static String buildAvatar(String name) {
            if (name == null || name.isBlank()) {
                return "R";
            }
            String[] parts = name.trim().split("\\s+");
            if (parts.length == 1) {
                return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase(Locale.ROOT);
            }
            return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase(Locale.ROOT);
        }

        private static String resolveGroup(String id) {
            if (id == null) {
                return "Khách";
            }
            if (id.startsWith("SV")) {
                return "Sinh viên";
            }
            if (id.startsWith("GV") || id.startsWith("A")) {
                return "Giảng viên";
            }
            return "Khách";
        }



        public BooleanProperty selectedProperty() {
            return selected;
        }

        public String getAccount() {
            return account.get();
        }

        public String getFullName() {
            return fullName.get();
        }

        public String getPhone() {
            return phone.get();
        }

        public String getEmail() {
            return email.get();
        }

        public String getDepartment() {
            return department.get();
        }

        public String getAddress() {
            return address.get();
        }

        public String getStatus() {
            return status.get();
        }

        public String getAvatarText() {
            return avatarText.get();
        }

        public String getGroup() {
            return group.get();
        }
    }
}
