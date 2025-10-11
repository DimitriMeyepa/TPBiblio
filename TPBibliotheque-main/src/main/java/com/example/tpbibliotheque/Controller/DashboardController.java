package com.example.tpbibliotheque.Controller;

import com.example.tpbibliotheque.DAO.DBconnection;
import com.example.tpbibliotheque.DAO.EleveDAO;
import com.example.tpbibliotheque.DAO.EmpruntDAO;
import com.example.tpbibliotheque.DAO.LivreDAO;
import com.example.tpbibliotheque.Service.EmailService;
import com.example.tpbibliotheque.Service.EmpruntService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import java.io.IOException;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.sql.SQLException;

public class DashboardController {

    private EmpruntDAO empruntDAO;
    private EleveDAO eleveDAO;
    private LivreDAO livreDAO;

    @FXML
    private Label lblNombreEleves;
    @FXML
    private Label lblNombreLivres;
    @FXML
    private Label lblNombreEmprunts;

    @FXML
    private BorderPane mainBorderPane; // Le BorderPane du Dashboard

    public void setDAOs(EmpruntDAO empruntDAO, EleveDAO eleveDAO, LivreDAO livreDAO) {
        this.empruntDAO = empruntDAO;
        this.eleveDAO = eleveDAO;
        this.livreDAO = livreDAO;
        updateStats();
    }

    @FXML
    public void initialize() {
        try {
            if (eleveDAO == null) eleveDAO = new EleveDAO(DBconnection.getConnection());
            if (livreDAO == null) livreDAO = new LivreDAO(DBconnection.getConnection());
            if (empruntDAO == null) empruntDAO = new EmpruntDAO(DBconnection.getConnection());

            updateStats();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateStats() {
        if (lblNombreEleves == null || lblNombreLivres == null || lblNombreEmprunts == null) return;

        try {
            lblNombreEleves.setText(String.valueOf(eleveDAO.countEleves()));
            lblNombreLivres.setText(String.valueOf(livreDAO.countLivres()));
            lblNombreEmprunts.setText(String.valueOf(empruntDAO.countEmprunts()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openEleveApp() {
        loadFXML("/com/example/tpbibliotheque/eleve.fxml", controller -> {
            ((EleveController) controller).setDAO(eleveDAO);
        });
    }

    @FXML
    private void openLivreApp() {
        loadFXML("/com/example/tpbibliotheque/livre.fxml", controller -> {
            ((LivreController) controller).setDAO(livreDAO);
        });
    }

    @FXML
    private void openEmpruntApp() {
        loadFXML("/com/example/tpbibliotheque/emprunt.fxml", controller -> {
            try {
                EmpruntController empruntController = (EmpruntController) controller;
                EmailService emailService = new EmailService("cdibiblio5@gmail.com", "pyswwuowovsrouno", true);
                EmpruntService empruntService = new EmpruntService(DBconnection.getConnection(), eleveDAO, emailService, livreDAO);
                empruntController.setServices(empruntService, eleveDAO, livreDAO);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void openRetourApp() {
        loadFXML("/com/example/tpbibliotheque/retour.fxml", controller -> {
            ((RetourController) controller).setDAO(empruntDAO);
        });
    }
    @FXML
    private void openDashboard() {
        loadFXML("/com/example/tpbibliotheque/dashboard_center.fxml", controller -> {
            ((DashboardCenterController) controller).setDAOs(eleveDAO, livreDAO, empruntDAO);
        });
    }
    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tpbibliotheque/Login.fxml"));
            Scene loginScene = new Scene(loader.load());

            Stage stage = (Stage) mainBorderPane.getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("Connexion");

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Impossible de se déconnecter.");
            alert.showAndWait();
        }
    }


    private void loadFXML(String fxmlPath, ControllerInitializer initializer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Pane pane = loader.load();
            if (initializer != null) {
                initializer.init(loader.getController());
            }
            mainBorderPane.setCenter(pane);
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible de charger la section");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FunctionalInterface
    private interface ControllerInitializer {
        void init(Object controller);
    }
}
