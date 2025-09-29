package com.example.tpbibliotheque.Controller;

import com.example.tpbibliotheque.DAO.EleveDAO;
import com.example.tpbibliotheque.DAO.LivreDAO;
import com.example.tpbibliotheque.Service.EmpruntService;
import com.example.tpbibliotheque.Utils.PDFUtil;
import com.example.tpbibliotheque.model.Eleve;
import com.example.tpbibliotheque.model.Emprunt;
import com.example.tpbibliotheque.model.Livre;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.time.LocalDate;
import java.sql.SQLException;
import java.io.File;
import java.util.List;

public class EmpruntController {

    @FXML private ComboBox<Eleve> eleveCombo;
    @FXML private ComboBox<Livre> livreCombo;
    @FXML private Button btnEmprunter;
    @FXML private Button btnPreview;
    @FXML private Label lblStatus;

    private EleveDAO eleveDAO;
    private LivreDAO livreDAO;
    private EmpruntService empruntService;

    public void setServices(EmpruntService empruntService, EleveDAO eleveDAO, LivreDAO livreDAO) {
        this.empruntService = empruntService;
        this.eleveDAO = eleveDAO;
        this.livreDAO = livreDAO;
        refreshCombo();
        setupLivreCombo();
    }

    @FXML
    private void initialize() {
        btnPreview.setDisable(true);
        eleveCombo.valueProperty().addListener((obs, oldVal, newVal) -> updatePreviewButton());
        livreCombo.valueProperty().addListener((obs, oldVal, newVal) -> updatePreviewButton());
    }

    private void updatePreviewButton() {
        btnPreview.setDisable(eleveCombo.getValue() == null || livreCombo.getValue() == null);
    }

    private void refreshCombo() {
        try {
            eleveCombo.setItems(FXCollections.observableArrayList(eleveDAO.readAll()));
            ObservableList<Livre> livres = FXCollections.observableArrayList(livreDAO.readAll());
            livreCombo.setItems(livres);
            setupLivreCombo();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // **Nouvelle méthode pour griser les livres empruntés**
    private void setupLivreCombo() {
        livreCombo.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Livre> call(ListView<Livre> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Livre item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setDisable(false);
                        } else {
                            setText(item.getTitre());
                            boolean emprunte = false;
                            try {
                                emprunte = livreDAO.isEmprunte(item.getCodeISBN());
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            setDisable(emprunte);
                            if (emprunte) setStyle("-fx-text-fill: gray;");
                            else setStyle("-fx-text-fill: black;");
                        }
                    }
                };
            }
        });

        // Affiche la même chose dans le bouton de sélection
        livreCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Livre item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getTitre());
                }
            }
        });
    }

    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void previewRecu() {
        Eleve eleve = eleveCombo.getValue();
        Livre livre = livreCombo.getValue();

        if (eleve == null || livre == null) {
            showAlert("Erreur", "Veuillez sélectionner un élève et un livre.");
            return;
        }

        Emprunt emprunt = new Emprunt();
        emprunt.setEleve(eleve);
        emprunt.setLivre(livre);
        emprunt.setDateEmprunt(LocalDate.now());

        try {
            File pdf = PDFUtil.genererPDFRecu(emprunt);
            java.awt.Desktop.getDesktop().open(pdf);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de générer le PDF : " + e.getMessage());
        }
    }

    @FXML
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
            lblStatus.setText("Emprunt validé.");
            showAlert("Succès", "Emprunt enregistré avec succès et PDF envoyé à l'élève.");
            eleveCombo.getScene().getWindow().hide();
            refreshCombo();
        });

        task.setOnFailed(ev -> {
            btnEmprunter.setDisable(false);
            lblStatus.setText("Échec lors de l'emprunt : " +
                    (task.getException() != null ? task.getException().getMessage() : "inconnu"));
        });

        new Thread(task, "emprunt-task").start();
    }
}
