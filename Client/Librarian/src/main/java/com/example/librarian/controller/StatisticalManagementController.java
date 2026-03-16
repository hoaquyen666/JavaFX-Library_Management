package com.example.librarian.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Label;
import javafx.collections.FXCollections;

import java.util.List;
import java.util.Map;
import com.example.librarian.model.ReaderStat;
import com.example.librarian.dao.BookDAO;
import com.example.librarian.dao.BorrowDAO;
import com.example.librarian.model.BookBorrowStat;
public class StatisticalManagementController {

    @FXML
    private BarChart<Number, String> categoryChart;

    @FXML
    private LineChart<String, Number> borrowTrendChart;

    @FXML
    private TableView<BookBorrowStat> topBooksTable;

    @FXML
    private TableView<ReaderStat> topReadersTable;

    @FXML
    private TableColumn<ReaderStat,String> colReaderName;

    @FXML
    private TableColumn<ReaderStat,Integer> colReaderTotal;

    @FXML
    private TableColumn<BookBorrowStat, String> colTitle;

    @FXML
    private TableColumn<BookBorrowStat, Integer> colTotal;

    @FXML
    private ComboBox<String> monthFilter;

    @FXML
    private Label totalBooksLabel;

    @FXML
    private Label totalReadersLabel;

    @FXML
    private Label borrowingLabel;

    @FXML
    private Label overdueLabel;


    private BookDAO bookDAO = new BookDAO();
    private BorrowDAO borrowDAO = new BorrowDAO();

    @FXML
    public void initialize() {
        loadStatistics();
        loadCategoryChart();
        loadBorrowTrendChart();
        loadTopBooks();
        loadTopReaders();
        loadMonthFilter();

    }

    @FXML
    private void refreshData() {

        System.out.println("Đã bấm nút Làm mới");
        loadCategoryChart();
        loadBorrowTrendChart();
        loadTopBooks();
    }

    private void loadCategoryChart(){

        Map<String,Integer> data = bookDAO.countBooksByCategory();

        XYChart.Series<Number,String> series = new XYChart.Series<>();

        for(String category : data.keySet()){
            series.getData().add(
                    new XYChart.Data<>(data.get(category), category)
            );
        }

        NumberAxis xAxis = (NumberAxis) categoryChart.getXAxis();
        CategoryAxis yAxis = (CategoryAxis) categoryChart.getYAxis();

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

        categoryChart.getData().clear();
        categoryChart.getData().add(series);


    }

    private void loadBorrowTrendChart(){

        Map<String,Integer> data = borrowDAO.countBorrowByMonth();

        XYChart.Series<String,Number> series = new XYChart.Series<>();

        for(String month : data.keySet()){

            series.getData().add(
                    new XYChart.Data<>(month, data.get(month))
            );

        }
        NumberAxis yAxis = (NumberAxis) borrowTrendChart.getYAxis();

        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(5);   // tùy dữ liệu
        yAxis.setTickUnit(1);     // mỗi vạch là số nguyên

        borrowTrendChart.getData().clear();
        borrowTrendChart.getData().add(series);

    }

    private void loadTopBooks(){

        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        List<BookBorrowStat> list = borrowDAO.getTopBorrowedBooks();

        topBooksTable.setItems(FXCollections.observableArrayList(list));

    }

    private void loadTopReaders(){

        colReaderName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colReaderTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        List<ReaderStat> list = borrowDAO.getTopReaders();

        topReadersTable.setItems(FXCollections.observableArrayList(list));
    }

    private void loadMonthFilter(){

        monthFilter.getItems().add("Tất cả");

        for(int i = 1; i <= 12; i++){
            monthFilter.getItems().add("Tháng " + i);
        }

        monthFilter.setValue("Tất cả");
    }

    private void loadStatistics(){

        totalBooksLabel.setText(String.valueOf(bookDAO.countBooks()));
        totalReadersLabel.setText(String.valueOf(borrowDAO.countReaders()));
        borrowingLabel.setText(String.valueOf(borrowDAO.countBorrowing()));
        overdueLabel.setText(String.valueOf(borrowDAO.countOverdue()));

    }
}