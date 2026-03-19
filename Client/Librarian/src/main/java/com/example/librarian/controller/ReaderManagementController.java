package com.example.librarian.controller;

import com.example.librarian.dao.ReaderDAO;
import com.example.librarian.model.ReaderRecord;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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

import java.util.List;
import java.util.Locale;

public class ReaderManagementController {

    @FXML
    private ComboBox<String> groupFilter;

    // Ô nhập từ khóa tìm kiếm.
    @FXML
    private TextField searchField;

    // Bảng hiển thị dữ liệu độc giả.
    @FXML
    private TableView<ReaderRow> readerTable;

    // Các cột hiển thị trong bảng.
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
    private TableColumn<ReaderRow, String> colAddress;
    @FXML
    private TableColumn<ReaderRow, String> colStatus;

    // Label tổng số bản ghi đang hiển thị sau lọc.
    @FXML
    private Label totalCountLabel;

    // UI phân trang. Hiện controller mới nạp dữ liệu lên combo, chưa xử lý phân trang thật.
    @FXML
    private ComboBox<String> pageSizeCombo;

    // Các nút hành động chính của màn hình.
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnEdit;
    @FXML
    private Button btnResetPassword;

    // Popup overlay dùng chung cho cả thêm và sửa độc giả.
    @FXML
    private StackPane readerPopupOverlay;
    @FXML
    private Label readerPopupTitle;
    @FXML
    private Label readerCodeCaption;
    @FXML
    private Label readerCodeValue;
    @FXML
    private TextField popupFullNameField;
    @FXML
    private TextField popupPhoneField;
    @FXML
    private TextField popupEmailField;
    @FXML
    private ComboBox<String> popupStatusCombo;

    // masterRows giữ dữ liệu gốc lấy từ DB.
    // filteredRows là lớp bọc để áp điều kiện filter lên masterRows.
    private final ObservableList<ReaderRow> masterRows = FXCollections.observableArrayList();
    private FilteredList<ReaderRow> filteredRows;

    // editingReader == null  -> popup đang ở mode thêm mới.
    // editingReader != null  -> popup đang ở mode chỉnh sửa.
    private ReaderRow editingReader;

    @FXML
    private void initialize() {
        // Trình tự khởi tạo:
        // 1. dựng filter,
        // 2. cấu hình bảng,
        // 3. bind nguồn dữ liệu,
        // 4. đọc dữ liệu từ DB,
        // 5. lọc lần đầu.
        setupFilters();
        setupTable();
        bindTableData();
        loadReadersFromDatabase();
        applyFilter();

        // Hai trạng thái cho popup thêm/sửa độc giả.
        popupStatusCombo.setItems(FXCollections.observableArrayList("Kích hoạt", "Khóa"));

        // Click ra vùng nền tối để đóng popup.
        readerPopupOverlay.setOnMouseClicked(event -> {
            if (event.getTarget() == readerPopupOverlay) {
                closeReaderPopup();
            }
        });

        // Khi chưa chọn dòng nào thì vô hiệu hóa nút sửa và reset password.
        readerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean disable = newVal == null;
            btnEdit.setDisable(disable);
            btnResetPassword.setDisable(disable);
        });
    }

    @FXML
    private void onSearchAction() {
        // Nút "Tìm" chỉ gọi lại logic filter.
        // Ngoài ra, controller cũng đã lọc realtime khi người dùng gõ.
        applyFilter();
    }

    private void setupFilters() {
        // Nhóm quyền ở đây được suy luận từ mã độc giả, không đọc trực tiếp từ DB.
        groupFilter.setItems(FXCollections.observableArrayList(
                "Tất cả nhóm quyền", "Sinh viên", "Giảng viên", "Khách"
        ));
        groupFilter.getSelectionModel().selectFirst();

        pageSizeCombo.setItems(FXCollections.observableArrayList("20 / trang", "50 / trang", "100 / trang"));
        pageSizeCombo.getSelectionModel().selectFirst();

        // Cứ thay đổi filter hoặc nội dung search là áp filter lại.
        groupFilter.valueProperty().addListener((obs, oldValue, newValue) -> applyFilter());
        searchField.textProperty().addListener((obs, oldValue, newValue) -> applyFilter());
    }

    private void setupTable() {
        readerTable.setEditable(true);

        // Ẩn placeholder mặc định của JavaFX khi bảng rỗng.
        readerTable.setPlaceholder(new Label(""));

        // Tự co giãn cột để tận dụng chiều ngang.
        readerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        readerTable.setFixedCellSize(40);

        // Cột checkbox được bind trực tiếp với BooleanProperty trong ReaderRow.
        colSelect.setCellValueFactory(cell -> cell.getValue().selectedProperty());
        colSelect.setCellFactory(CheckBoxTableCell.forTableColumn(colSelect));
        colSelect.setSortable(false);
        colSelect.setEditable(true);
        colSelect.setReorderable(false);

        // Avatar ở đây không phải ảnh thật.
        // Nó là chữ viết tắt của họ tên được dựng bằng ReaderRow.buildAvatar(...).
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

        // PropertyValueFactory("account") sẽ gọi getAccount() trong ReaderRow.
        // Tương tự cho fullName, phone, email, address.
        colAccount.setCellValueFactory(new PropertyValueFactory<>("account"));
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));

        // Cột trạng thái dùng label dạng chip màu để nhìn rõ hơn.
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
                chip.getStyleClass().add("status-chip");

                // Nếu status là "Khóa" thì tô màu khác để phân biệt với trạng thái hoạt động.
                if ("Khóa".equalsIgnoreCase(item)) {
                    chip.getStyleClass().add("status-locked");
                } else {
                    chip.getStyleClass().add("status-active");
                }

                setGraphic(chip);
                setText(null);
            }
        });
    }

    private void bindTableData() {
        // FilteredList cho phép thay đổi predicate để lọc dữ liệu mà không sửa masterRows.
        filteredRows = new FilteredList<>(masterRows, row -> true);

        // SortedList giúp tận dụng sort của TableView.
        SortedList<ReaderRow> sortedRows = new SortedList<>(filteredRows);
        sortedRows.comparatorProperty().bind(readerTable.comparatorProperty());

        // Từ đây về sau bảng luôn dùng dữ liệu sau lọc + sau sắp xếp.
        readerTable.setItems(sortedRows);
    }

    private void loadReadersFromDatabase() {
        ReaderDAO dao = new ReaderDAO();
        List<ReaderRecord> readers = dao.findAllReaders();

        masterRows.clear();

        for (ReaderRecord r : readers) {
            // ReaderRecord là model dữ liệu DB.
            // ReaderRow là model phục vụ UI, có thêm avatar/group/checkbox.
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

        // Chuẩn hóa text để so sánh không phân biệt hoa thường và tránh khoảng trắng thừa.
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
                    || normalize(row.getAddress()).contains(keyword);

            return groupMatched && keywordMatched;
        });

        totalCountLabel.setText(filteredRows.size() + " bản ghi");
    }

    private static String normalize(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT).trim();
    }


    //Hàm thêm độc giả, hiển thị PopUp
    @FXML
    private void onAddReader() {
        // Mode thêm => xóa tham chiếu đối tượng đang sửa.
        editingReader = null;

        // Reset popup về trạng thái rỗng để nhập mới.
        readerPopupTitle.setText("Thêm Độc Giả Mới");
        readerCodeCaption.setText("Mã độc giả sẽ được tạo tự động");
        readerCodeValue.setText("--");
        popupFullNameField.clear();
        popupPhoneField.clear();
        popupEmailField.clear();
        popupStatusCombo.getSelectionModel().selectFirst();

        readerPopupOverlay.setVisible(true);
    }

    @FXML
    private void onEditReader() {
        ReaderRow selected = readerTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn độc giả cần sửa.");
            return;
        }

        // Lưu lại dòng được chọn để saveReader biết đang UPDATE chứ không phải INSERT.
        editingReader = selected;

        // Đổ dữ liệu hiện tại của dòng lên popup.
        readerPopupTitle.setText("Sửa Thông Tin Độc Giả");
        readerCodeCaption.setText("Mã độc giả");
        readerCodeValue.setText(selected.getReaderCode());
        popupFullNameField.setText(selected.getFullName());
        popupPhoneField.setText(selected.getPhone());
        popupEmailField.setText(selected.getEmail());
        popupStatusCombo.setValue(selected.getStatus());

        readerPopupOverlay.setVisible(true);
    }

    @FXML
    private void closeReaderPopup() {
        readerPopupOverlay.setVisible(false);
    }

    @FXML
    private void saveReader() {
        // Lấy dữ liệu từ form popup.
        String fullName = popupFullNameField.getText() == null ? "" : popupFullNameField.getText().trim();
        String phone = popupPhoneField.getText() == null ? "" : popupPhoneField.getText().trim();
        String email = popupEmailField.getText() == null ? "" : popupEmailField.getText().trim();
        String status = popupStatusCombo.getValue();

        // Validation hiện tại mới ở mức cơ bản:
        // - không để trống
        // - chưa kiểm tra định dạng email/số điện thoại.
        if (fullName.isBlank() || phone.isBlank() || email.isBlank() || status == null || status.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập đầy đủ họ tên, điện thoại, email và trạng thái.");
            return;
        }

        ReaderDAO dao = new ReaderDAO();

        if (editingReader == null) {
            // THÊM MỚI:
            // Mã độc giả được tạo tạm bằng timestamp.
            // Cách này nhanh nhưng không đẹp và khó kiểm soát quy chuẩn mã về lâu dài.
            ReaderRecord reader = new ReaderRecord(
                    "SV" + System.currentTimeMillis(),
                    "",
                    fullName,
                    phone,
                    email,
                    status
            );
            dao.addReader(reader);
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã thêm độc giả mới.");
        } else {
            // CẬP NHẬT:
            // Giữ nguyên mã độc giả và account cũ, chỉ sửa thông tin thay đổi.
            ReaderRecord reader = new ReaderRecord(
                    editingReader.getReaderCode(),
                    editingReader.getAccount(),
                    fullName,
                    phone,
                    email,
                    status
            );
            dao.updateReader(reader);
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã cập nhật thông tin độc giả.");
        }

        // Sau khi lưu, reload lại dữ liệu để UI khớp hoàn toàn với DB.
        closeReaderPopup();
        loadReadersFromDatabase();
        applyFilter();
    }

    @FXML
    private void onResetPassword() {
        ReaderRow selected = readerTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        ReaderDAO dao = new ReaderDAO();

        // Reset password ở đây chỉ gọi DAO set cứng mật khẩu về 123456.
        dao.resetPassword(selected.getAccount());
        showAlert(Alert.AlertType.INFORMATION, "Reset Password", "Password đã reset về: 123456");

        loadReadersFromDatabase();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        // Gom logic Alert để tránh lặp code ở nhiều nơi.
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static class ReaderRow {
        // selected phục vụ cột checkbox trong TableView.
        private final BooleanProperty selected = new SimpleBooleanProperty(false);

        // Các property phục vụ UI.
        // Dùng JavaFX Property giúp TableView binding dữ liệu thuận tiện hơn.
        private final StringProperty readerCode = new SimpleStringProperty();
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
            this.readerCode.set(id);
            this.account.set(account);
            this.fullName.set(fullName);
            this.phone.set(phone);
            this.email.set(email);
            this.department.set(department);
            this.address.set(address);
            this.status.set(status);

            // Hai field này được "suy luận" để phục vụ hiển thị.
            this.avatarText.set(buildAvatar(fullName));
            this.group.set(resolveGroup(id));
        }

        private static String buildAvatar(String name) {
            // Nếu tên trống thì dùng ký tự mặc định.
            if (name == null || name.isBlank()) {
                return "R";
            }

            String[] parts = name.trim().split("\\s+");

            // Nếu chỉ có một từ thì lấy tối đa 2 ký tự đầu.
            if (parts.length == 1) {
                return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase(Locale.ROOT);
            }

            // Nếu nhiều từ thì lấy chữ cái đầu của từ đầu và từ cuối.
            return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase(Locale.ROOT);
        }

        private static String resolveGroup(String id) {
            // Suy luận nhóm độc giả từ prefix của ReaderCode.
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

        public String getReaderCode() {
            return readerCode.get();
        }
    }
}
