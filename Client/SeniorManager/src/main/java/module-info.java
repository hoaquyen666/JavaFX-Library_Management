module com.example.seniormanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.materialdesign2;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires jakarta.mail;
    requires jakarta.activation;
    requires java.sql;

    exports com.example.seniormanager;
    opens com.example.seniormanager.controller to javafx.fxml, javafx.base;
    opens com.example.seniormanager.util to javafx.base, javafx.fxml;
    opens com.example.seniormanager.model to javafx.base;
    opens com.example.seniormanager.controller.StaffView to javafx.base, javafx.fxml;
    opens com.example.seniormanager.controller.AccountView to javafx.base, javafx.fxml;
    opens com.example.seniormanager.controller.Shift to javafx.base, javafx.fxml;

}