package com.example.tpbibliotheque.Controller;

import com.example.tpbibliotheque.DAO.EleveDAO;
import com.example.tpbibliotheque.model.Eleve;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;

public class EleveController {

    @FXML
    private TextField nomField, prenomField, classeField, emailField;

    @FXML
    private TextField rechercheField;

    @FXML
    private TableView<Eleve> eleveTable;

    @FXML
    private TableColumn<Eleve, Integer> idColumn;

    @FXML
    private TableColumn<Eleve, String> nomColumn;

    @FXML
    private TableColumn<Eleve, String> prenomColumn;

    @FXML
    private TableColumn<Eleve, String> classeColumn;

    @FXML
    private TableColumn<Eleve, String> emailColumn;

    @FXML
    private Button ajouterButton, modifierButton, supprimerButton, annulerButton;

    private EleveDAO dao = new EleveDAO();
    private ObservableList<Eleve> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idEleve"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        classeColumn.setCellValueFactory(new PropertyValueFactory<>("classe"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        eleveTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        eleveTable.setItems(data);

        modifierButton.setDisable(true);
        supprimerButton.setDisable(true);
        ajouterButton.setDisable(false);
        annulerButton.setVisible(false);

        eleveTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean selected = newSelection != null;

            modifierButton.setDisable(!selected);
            supprimerButton.setDisable(!selected);
            ajouterButton.setDisable(selected);
            annulerButton.setVisible(selected);
            rechercheField.setDisable(selected);

            if (selected) {
                nomField.setText(newSelection.getNom());
                prenomField.setText(newSelection.getPrenom());
                classeField.setText(newSelection.getClasse());
                emailField.setText(newSelection.getEmail());
            } else {
                clearFields();
            }
        });

        refresh();

        FilteredList<Eleve> filteredData = new FilteredList<>(data, e -> true);
        rechercheField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(eleve -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return eleve.getNom().toLowerCase().contains(lowerCaseFilter);
            });
        });

        SortedList<Eleve> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(eleveTable.comparatorProperty());
        eleveTable.setItems(sortedData);

        rechercheField.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, event -> {
                    if (rechercheField.isFocused() && !rechercheField.equals(event.getTarget())
                            && !rechercheField.lookupAll(".text").contains(event.getTarget())) {
                        eleveTable.requestFocus();
                    }
                });
            }
        });
    }

    public void setDAO(EleveDAO dao) {
        this.dao = dao;
    }

    @FXML
    public void ajouterEleve() throws SQLException {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String classe = classeField.getText().trim();
        String email = emailField.getText().trim();

        if (nom.isEmpty() || prenom.isEmpty() || classe.isEmpty() || email.isEmpty()) {
            showError("Erreur de saisie", "Tous les champs doivent être saisis !");
            return;
        }

        Eleve e = new Eleve();
        e.setNom(nom);
        e.setPrenom(prenom);
        e.setClasse(classe);
        e.setEmail(email);

        dao.create(e);
        clearFields();
        refresh();
        showAlert("Succès", "L'élève a bien été ajouté !");
    }

    @FXML
    public void supprimerEleve() throws SQLException {
        Eleve e = eleveTable.getSelectionModel().getSelectedItem();
        if (e != null) {
            dao.delete(e.getIdEleve());
            refresh();
            showAlert("Succès", "L'élève a bien été supprimé !");
        } else {
            showAlert("Avertissement", "Veuillez sélectionner un élève à supprimer.");
        }
    }

    @FXML
    public void modifierEleve() throws SQLException {
        Eleve selected = eleveTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setNom(nomField.getText());
            selected.setPrenom(prenomField.getText());
            selected.setClasse(classeField.getText());
            selected.setEmail(emailField.getText());

            dao.update(selected);
            clearFields();
            refresh();
            showAlert("Succès", "L'élève a bien été modifié !");
        } else {
            showAlert("Avertissement", "Veuillez sélectionner un élève à modifier.");
        }
    }

    @FXML
    private void annulerSelection() {
        eleveTable.getSelectionModel().clearSelection();
        clearFields();
    }

    private void refresh() {
        try {
            data.setAll(dao.readAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        nomField.clear();
        prenomField.clear();
        classeField.clear();
        emailField.clear();
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
