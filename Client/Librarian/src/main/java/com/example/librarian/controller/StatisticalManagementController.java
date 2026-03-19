package com.example.librarian.controller;

import com.example.librarian.dao.BookDAO;
import com.example.librarian.dao.BorrowDAO;
import com.example.librarian.model.BookBorrowStat;
import com.example.librarian.model.ReaderStat;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.Map;

public class StatisticalManagementController {

    // categoryChart là biểu đồ cột ngang:
    // - trục X: số lượng sách,
    // - trục Y: tên thể loại.
    @FXML
    private BarChart<Number, String> categoryChart;

    // borrowTrendChart là biểu đồ đường:
    // - trục X: tháng,
    // - trục Y: số lượt mượn.
    @FXML
    private LineChart<String, Number> borrowTrendChart;

    // Hai bảng top dùng để hiển thị dữ liệu thống kê chi tiết hơn.
    @FXML
    private TableView<BookBorrowStat> topBooksTable;
    @FXML
    private TableView<ReaderStat> topReadersTable;

    // Cột của bảng top độc giả.
    @FXML
    private TableColumn<ReaderStat, String> colReaderName;
    @FXML
    private TableColumn<ReaderStat, Integer> colReaderTotal;

    // Cột của bảng top sách.
    @FXML
    private TableColumn<BookBorrowStat, String> colTitle;
    @FXML
    private TableColumn<BookBorrowStat, Integer> colTotal;

    // Combobox lọc tháng.
    // Hiện tại controller mới chỉ nạp dữ liệu cho nó, chưa áp giá trị chọn vào truy vấn thống kê.
    @FXML
    private ComboBox<String> monthFilter;

    // 4 label KPI ở đầu trang.
    @FXML
    private Label totalBooksLabel;
    @FXML
    private Label totalReadersLabel;
    @FXML
    private Label borrowingLabel;
    @FXML
    private Label overdueLabel;

    // DAO chịu trách nhiệm lấy dữ liệu từ database.
    // Controller chỉ điều phối dữ liệu và gắn vào UI.
    private final BookDAO bookDAO = new BookDAO();
    private final BorrowDAO borrowDAO = new BorrowDAO();

    @FXML
    public void initialize() {
        // Khi màn hình thống kê được load:
        // 1. nạp KPI,
        // 2. nạp biểu đồ,
        // 3. nạp bảng top,
        // 4. nạp combobox tháng.
        loadStatistics();
        loadCategoryChart();
        loadBorrowTrendChart();
        loadTopBooks();
        loadTopReaders();
        loadMonthFilter();
    }

    @FXML
    private void refreshData() {
        // Nút "Làm mới" hiện chỉ refresh một phần dữ liệu.
        // Nó chưa reload lại topReaders, KPI, cũng chưa dựa trên monthFilter.
        System.out.println("Đã bấm nút Làm mới");
        loadCategoryChart();
        loadBorrowTrendChart();
        loadTopBooks();
    }

    private void loadCategoryChart() {
        // DAO trả về Map:
        // key   = tên thể loại,
        // value = số lượng sách thuộc thể loại đó.
        Map<String, Integer> data = bookDAO.countBooksByCategory();

        XYChart.Series<Number, String> series = new XYChart.Series<>();

        // Với BarChart<Number, String>, thứ tự Data là (giá trị số, nhãn category).
        for (String category : data.keySet()) {
            series.getData().add(
                    new XYChart.Data<>(data.get(category), category)
            );
        }

        // Lấy trực tiếp hai trục để cấu hình hiển thị.
        NumberAxis xAxis = (NumberAxis) categoryChart.getXAxis();
        CategoryAxis yAxis = (CategoryAxis) categoryChart.getYAxis();

        // Trục X đang bị set cứng từ 0 đến 20.
        // Nếu số lượng sách thực tế vượt 20 thì biểu đồ có thể hiển thị không đủ.
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(20);
        xAxis.setTickUnit(5);

        xAxis.setStyle("-fx-font-size:20px; -fx-font-weight:bold;");
        yAxis.setStyle("-fx-font-size:20px; -fx-font-weight:bold;");

        categoryChart.setStyle("-fx-font-size:16px;");
        categoryChart.setCategoryGap(15);
        categoryChart.setBarGap(2);
        categoryChart.setLegendVisible(false);

        // Clear trước để tránh mỗi lần refresh bị cộng dồn series cũ.
        categoryChart.getData().clear();
        categoryChart.getData().add(series);
    }

    private void loadBorrowTrendChart() {
        // DAO trả về Map:
        // key   = tháng,
        // value = số lượt mượn của tháng đó.
        Map<String, Integer> data = borrowDAO.countBorrowByMonth();

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        for (String month : data.keySet()) {
            series.getData().add(
                    new XYChart.Data<>(month, data.get(month))
            );
        }

        NumberAxis yAxis = (NumberAxis) borrowTrendChart.getYAxis();

        // Trục Y cũng đang set cứng 0 -> 5.
        // Đây là cấu hình dễ hiểu khi demo, nhưng không linh hoạt cho dữ liệu thật.
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(5);
        yAxis.setTickUnit(1);

        borrowTrendChart.getData().clear();
        borrowTrendChart.getData().add(series);
    }

    private void loadTopBooks() {
        // PropertyValueFactory("title") sẽ tự gọi getTitle() trong BookBorrowStat.
        // PropertyValueFactory("total") sẽ tự gọi getTotal().
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        List<BookBorrowStat> list = borrowDAO.getTopBorrowedBooks();
        topBooksTable.setItems(FXCollections.observableArrayList(list));
    }

    private void loadTopReaders() {
        // PropertyValueFactory("name") -> getName()
        // PropertyValueFactory("total") -> getTotal()
        colReaderName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colReaderTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        List<ReaderStat> list = borrowDAO.getTopReaders();
        topReadersTable.setItems(FXCollections.observableArrayList(list));
    }

    private void loadMonthFilter() {
        // Nạp option tổng quát trước.
        monthFilter.getItems().add("Tất cả");

        // Sau đó nạp 12 tháng trong năm.
        for (int i = 1; i <= 12; i++) {
            monthFilter.getItems().add("Tháng " + i);
        }

        // Mặc định lúc mở màn hình là "Tất cả".
        monthFilter.setValue("Tất cả");
    }

    private void loadStatistics() {
        // 4 hàm DAO bên dưới trả về số liệu để hiển thị ở các ô thống kê đầu trang.
        totalBooksLabel.setText(String.valueOf(bookDAO.countBooks()));
        totalReadersLabel.setText(String.valueOf(borrowDAO.countReaders()));
        borrowingLabel.setText(String.valueOf(borrowDAO.countBorrowing()));
        overdueLabel.setText(String.valueOf(borrowDAO.countOverdue()));
    }
}
