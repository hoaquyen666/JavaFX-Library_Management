module com.example.librarian {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.materialdesign2;


    opens com.example.librarian.controller to javafx.fxml, javafx.base;
    exports com.example.librarian;
}