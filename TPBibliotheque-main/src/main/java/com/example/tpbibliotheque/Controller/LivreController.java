package com.example.tpbibliotheque.Controller;

import com.example.tpbibliotheque.DAO.LivreDAO;
import com.example.tpbibliotheque.model.Livre;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;

public class LivreController {

    @FXML private TextField titreField, auteurField, isbnField, anneeField;
    @FXML private TableView<Livre> livreTable;
    @FXML private TableColumn<Livre, String> titreColumn;
    @FXML private TableColumn<Livre, String> auteurColumn;
    @FXML private TableColumn<Livre, String> isbnColumn;
    @FXML private TableColumn<Livre, Integer> anneeColumn;

    @FXML private Button ajouterButton;
    @FXML private Button modifierButton;
    @FXML private Button supprimerButton;
    @FXML private Button annulerButton;

    private LivreDAO dao;
    private ObservableList<Livre> data;

    @FXML
    public void initialize() {
        titreColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        auteurColumn.setCellValueFactory(new PropertyValueFactory<>("auteur"));
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("codeISBN"));
        anneeColumn.setCellValueFactory(new PropertyValueFactory<>("anneePublication"));

        livreTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        data = FXCollections.observableArrayList();
        livreTable.setItems(data);

        modifierButton.setDisable(true);
        supprimerButton.setDisable(true);
        ajouterButton.setDisable(false);
        annulerButton.setVisible(false);

        livreTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean selected = newSelection != null;

            modifierButton.setDisable(!selected);
            supprimerButton.setDisable(!selected);
            ajouterButton.setDisable(selected);
            annulerButton.setVisible(selected);

            if (selected) {
                titreField.setText(newSelection.getTitre());
                auteurField.setText(newSelection.getAuteur());
                isbnField.setText(newSelection.getCodeISBN());
                anneeField.setText(String.valueOf(newSelection.getAnneePublication()));
                isbnField.setDisable(true);
            } else {
                clearFields();
            }
        });
    }

    public void setDAO(LivreDAO dao) {
        this.dao = dao;
        refresh();
    }

    @FXML
    public void ajouterLivre() throws SQLException {
        String titre = titreField.getText().trim();
        String auteur = auteurField.getText().trim();
        String isbn = isbnField.getText().trim();
        String anneeText = anneeField.getText().trim();

        if (titre.isEmpty() || auteur.isEmpty() || isbn.isEmpty() || anneeText.isEmpty()) {
            showError("Erreur de saisie", "Tous les champs doivent être remplis !");
            return;
        }

        int annee;
        try {
            annee = Integer.parseInt(anneeText);
        } catch (NumberFormatException e) {
            showError("Erreur de saisie", "L'année doit être un nombre valide.");
            return;
        }

        Livre l = new Livre();
        l.setTitre(titre);
        l.setAuteur(auteur);
        l.setCodeISBN(isbn);
        l.setAnneePublication(annee);

        dao.create(l);
        clearFields();
        refresh();

        showAlert("Succès", "Le livre a bien été ajouté !");
    }

    @FXML
    public void modifierLivre() throws SQLException {
        Livre selected = livreTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            String titre = titreField.getText().trim();
            String auteur = auteurField.getText().trim();
            String anneeText = anneeField.getText().trim();

            if (titre.isEmpty() || auteur.isEmpty() || anneeText.isEmpty()) {
                showError("Erreur de saisie", "Tous les champs doivent être remplis !");
                return;
            }

            int annee;
            try {
                annee = Integer.parseInt(anneeText);
            } catch (NumberFormatException e) {
                showError("Erreur de saisie", "L'année doit être un nombre valide.");
                return;
            }

            selected.setTitre(titre);
            selected.setAuteur(auteur);
            selected.setAnneePublication(annee);
            dao.update(selected);
            clearFields();
            refresh();

            showAlert("Succès", "Le livre a bien été modifié !");
        } else {
            showAlert("Avertissement", "Veuillez sélectionner un livre à modifier.");
        }
    }

    @FXML
    public void supprimerLivre() {
        Livre l = livreTable.getSelectionModel().getSelectedItem();
        if (l != null) {
            try {
                dao.delete(l.getCodeISBN());
                clearFields();
                refresh();
                showAlert("Succès", "Le livre a bien été supprimé !");
            } catch (SQLException e) {
                // Ici on teste le message exact levé dans DAO
                if (e.getMessage().equals("Impossible de supprimer un livre non retourné.")) {
                    showError("Erreur de suppression", "Impossible de supprimer un livre non retourné.");
                } else {
                    e.printStackTrace();
                    showError("Erreur SQL", "Une erreur s'est produite lors de la suppression.");
                }
            }
        } else {
            showAlert("Avertissement", "Veuillez sélectionner un livre à supprimer.");
        }
    }

    @FXML
    private void annulerSelection() {
        livreTable.getSelectionModel().clearSelection();
        clearFields();
    }

    private void refresh() {
        if (dao == null) return;
        try {
            data.setAll(dao.readAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        titreField.clear();
        auteurField.clear();
        isbnField.clear();
        anneeField.clear();
        isbnField.setDisable(false);
        livreTable.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
