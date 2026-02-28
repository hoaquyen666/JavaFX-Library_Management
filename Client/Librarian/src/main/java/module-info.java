module com.example.librarian {
    requires javafx.controls;
    requires javafx.fxml;



    opens com.example.librarian.controller to javafx.fxml, javafx.base;
    exports com.example.librarian;
}