package com.example.tpbibliotheque;

import com.example.tpbibliotheque.Controller.DashboardController;
import com.example.tpbibliotheque.DAO.DBconnection;
import com.example.tpbibliotheque.DAO.EleveDAO;
import com.example.tpbibliotheque.DAO.EmpruntDAO;
import com.example.tpbibliotheque.DAO.LivreDAO;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.util.Objects;

public class Main extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;

        stage.getIcons().add(new javafx.scene.image.Image(
                Objects.requireNonNull(getClass().getResourceAsStream("/com/example/tpbibliotheque/images/logo.png"))
        ));

        showDashboard();
    }

    public void showDashboard() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tpbibliotheque/Dashboard.fxml"));
        Scene scene = new Scene(loader.load());

        primaryStage.setTitle("Gestion Bibliothèque");
        primaryStage.setScene(scene);

        Connection conn = DBconnection.getConnection();
        EleveDAO eleveDAO = new EleveDAO(conn);
        LivreDAO livreDAO = new LivreDAO(conn);
        EmpruntDAO empruntDAO = new EmpruntDAO(conn);

        DashboardController dashboardController = loader.getController();
        dashboardController.setDAOs(empruntDAO, eleveDAO, livreDAO);

        primaryStage.show();

        Platform.runLater(() -> {
            primaryStage.setMaximized(true);
            primaryStage.centerOnScreen();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
