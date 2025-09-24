package com.example.tpbibliotheque.Controller;

import com.example.tpbibliotheque.DAO.EmpruntDAO;
import com.example.tpbibliotheque.model.Emprunt;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.util.stream.Collectors;

import java.sql.SQLException;
import java.time.LocalDate;

public class RetourController {

    @FXML private TableView<Emprunt> empruntTable;
    @FXML private TableColumn<Emprunt, String> colEleve;
    @FXML private TableColumn<Emprunt, String> colLivre;
    @FXML private TableColumn<Emprunt, String> colDateEmprunt;
    @FXML private TableColumn<Emprunt, String> colDateRetour;

    private EmpruntDAO dao;
    private ObservableList<Emprunt> data;

    @FXML
    public void initialize() {
        colEleve.setCellValueFactory(cellData -> {
            if (cellData.getValue().getEleve() != null) {
                return new SimpleStringProperty(
                        cellData.getValue().getEleve().getNom() + " " + cellData.getValue().getEleve().getPrenom());
            } else {
                return new SimpleStringProperty("Inconnu");
            }
        });

        colLivre.setCellValueFactory(cellData -> {
            if (cellData.getValue().getLivre() != null) {
                return new SimpleStringProperty(cellData.getValue().getLivre().getTitre());
            } else {
                return new SimpleStringProperty("Inconnu");
            }
        });

        colDateEmprunt.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getDateEmprunt();
            return new SimpleStringProperty(date != null ? date.toString() : "");
        });

        colDateRetour.setCellValueFactory(cellData -> {
            LocalDate retour = cellData.getValue().getDateRetour();
            return new SimpleStringProperty(retour != null ? retour.toString() : "Non retourné");
        });
        empruntTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        data = FXCollections.observableArrayList();
        empruntTable.setItems(data);
    }

    public void setDAO(EmpruntDAO dao) {
        this.dao = dao;
        refresh();
    }

    public void marquerRetour() throws SQLException {
        Emprunt e = empruntTable.getSelectionModel().getSelectedItem();
        if (e != null && e.getDateRetour() == null) {
            dao.updateRetour(e.getId(), LocalDate.now());
            refresh();
        }
    }

    private void refresh() {
        try {
            data.setAll(
                    dao.readAll().stream()
                            .sorted((e1, e2) -> {
                                LocalDate d1 = e1.getDateRetour();
                                LocalDate d2 = e2.getDateRetour();
                                if (d1 == null && d2 == null) return 0;
                                if (d1 == null) return 1;
                                if (d2 == null) return -1;
                                return d1.compareTo(d2);
                            })
                            .collect(Collectors.toList())
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
