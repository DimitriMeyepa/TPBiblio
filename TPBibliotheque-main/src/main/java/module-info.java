module com.example.tpbibliotheque {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires jakarta.mail;
    requires kernel;
    requires layout;
    requires io;
    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires nanohttpd;
    requires html2pdf;
    opens com.example.tpbibliotheque to javafx.fxml;
    opens com.example.tpbibliotheque.Controller to javafx.fxml;
    opens com.example.tpbibliotheque.DAO to javafx.fxml;
    opens com.example.tpbibliotheque.model to javafx.fxml;

    exports com.example.tpbibliotheque;
    exports com.example.tpbibliotheque.Controller;
    exports com.example.tpbibliotheque.DAO;
    exports com.example.tpbibliotheque.model;
}
