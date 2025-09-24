package com.example.tpbibliotheque.Controller;

import com.example.tpbibliotheque.DAO.EleveDAO;
import com.example.tpbibliotheque.DAO.LivreDAO;
import com.example.tpbibliotheque.model.Eleve;
import com.example.tpbibliotheque.model.Emprunt;
import com.example.tpbibliotheque.model.Livre;
import com.example.tpbibliotheque.Service.EmpruntService;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

import java.time.LocalDate;
import java.sql.SQLException;

public class EmpruntController {

    @FXML private ComboBox<Eleve> eleveCombo;
    @FXML private ComboBox<Livre> livreCombo;
    @FXML private Button btnEmprunter;
    @FXML private Label lblStatus;

    private EleveDAO eleveDAO;
    private LivreDAO livreDAO;
    private EmpruntService empruntService;

    public void setServices(EmpruntService empruntService, EleveDAO eleveDAO, LivreDAO livreDAO) {
        this.empruntService = empruntService;
        this.eleveDAO = eleveDAO;
        this.livreDAO = livreDAO;
        refreshCombo();
    }

    public void enregistrerEmprunt() {
        Eleve eleveSelectionne = eleveCombo.getValue();
        Livre livreSelectionne = livreCombo.getValue();

        if (eleveSelectionne == null) {
            showAlert("Erreur", "Veuillez sélectionner un élève.");
            return;
        }

        if (livreSelectionne == null) {
            showAlert("Erreur", "Veuillez sélectionner un livre.");
            return;
        }

        Emprunt e = new Emprunt();
        e.setEleve(eleveSelectionne);
        e.setLivre(livreSelectionne);
        e.setDateEmprunt(LocalDate.now());
        e.setDateRetour(null);

        // Tâche en arrière-plan pour valider et envoyer l’email
        btnEmprunter.setDisable(true);
        lblStatus.setText("Enregistrement de l'emprunt…");

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                empruntService.validerEmprunt(eleveSelectionne.getIdEleve(), livreSelectionne.getCodeISBN());
                return null;
            }
        };

        task.setOnSucceeded(ev -> {
            btnEmprunter.setDisable(false);
            lblStatus.setText("Emprunt validé et email envoyé (si adresse).");
            showAlert("Succès", "Emprunt enregistré avec succès.");
            eleveCombo.getScene().getWindow().hide();
        });

        task.setOnFailed(ev -> {
            btnEmprunter.setDisable(false);
            lblStatus.setText("Échec lors de l'emprunt : " +
                    (task.getException() != null ? task.getException().getMessage() : "inconnu"));
        });

        new Thread(task, "emprunt-task").start();
    }

    private void refreshCombo() {
        try {
            eleveCombo.setItems(FXCollections.observableArrayList(eleveDAO.readAll()));
            livreCombo.setItems(FXCollections.observableArrayList(livreDAO.readAll()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
