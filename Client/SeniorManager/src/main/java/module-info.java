module com.example.seniormanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires jakarta.mail;
    requires jakarta.activation;

    exports com.example.seniormanager;
    opens com.example.seniormanager.controller to javafx.fxml;
}