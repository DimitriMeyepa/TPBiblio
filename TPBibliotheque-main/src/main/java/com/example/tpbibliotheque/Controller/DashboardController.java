package com.example.tpbibliotheque.Controller;

import com.example.tpbibliotheque.DAO.DBconnection;
import com.example.tpbibliotheque.DAO.EleveDAO;
import com.example.tpbibliotheque.DAO.EmpruntDAO;
import com.example.tpbibliotheque.DAO.LivreDAO;
import com.example.tpbibliotheque.Service.EmailService;
import com.example.tpbibliotheque.Service.EmpruntService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class DashboardController {

    private EmpruntDAO empruntDAO;
    private EleveDAO eleveDAO;
    private LivreDAO livreDAO;

    public void setDAOs(EmpruntDAO empruntDAO, EleveDAO eleveDAO, LivreDAO livreDAO) {
        this.empruntDAO = empruntDAO;
        this.eleveDAO = eleveDAO;
        this.livreDAO = livreDAO;
    }

    @FXML
    private void openEleveApp() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tpbibliotheque/eleve.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Gestion Élèves");

        EleveController controller = loader.getController();
        controller.setDAO(eleveDAO);

        stage.show();
    }

    @FXML
    private void openLivreApp() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tpbibliotheque/livre.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Gestion Livres");

        LivreController controller = loader.getController();
        controller.setDAO(livreDAO);

        stage.show();
    }

    @FXML
    private void openEmpruntApp() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tpbibliotheque/emprunt.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Gestion Emprunts");

        try {
            EmailService emailService = new EmailService(
                    "cdibiblio5@gmail.com",
                    "pyswwuowovsrouno",
                    true
            );

            EmpruntService empruntService = new EmpruntService(
                    DBconnection.getConnection(),
                    new EleveDAO(DBconnection.getConnection()),
                    emailService,
                    new LivreDAO(DBconnection.getConnection())
            );
            EmpruntController controller = loader.getController();
            controller.setServices(empruntService, eleveDAO, livreDAO);

            stage.show();

        } catch (SQLException ex) {
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Connexion impossible");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void openRetourApp() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tpbibliotheque/retour.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Gestion Retours");

        RetourController controller = loader.getController();
        controller.setDAO(empruntDAO);

        stage.show();
    }
}
