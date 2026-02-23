module com.example.librarian {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.librarian to javafx.fxml;
    exports com.example.librarian;
}