package com.example.tpbibliotheque.Controller;

import com.example.tpbibliotheque.DAO.EleveDAO;
import com.example.tpbibliotheque.DAO.EmpruntDAO;
import com.example.tpbibliotheque.DAO.LivreDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.sql.SQLException;

public class DashboardCenterController {

    private EleveDAO eleveDAO;
    private LivreDAO livreDAO;
    private EmpruntDAO empruntDAO;

    @FXML
    private Label lblNombreEleves;
    @FXML
    private Label lblNombreLivres;
    @FXML
    private Label lblNombreEmprunts;

    public void setDAOs(EleveDAO eleveDAO, LivreDAO livreDAO, EmpruntDAO empruntDAO) {
        this.eleveDAO = eleveDAO;
        this.livreDAO = livreDAO;
        this.empruntDAO = empruntDAO;
        updateStats();
    }

    private void updateStats() {
        try {
            lblNombreEleves.setText(String.valueOf(eleveDAO.countEleves()));
            lblNombreLivres.setText(String.valueOf(livreDAO.countLivres()));
            lblNombreEmprunts.setText(String.valueOf(empruntDAO.countEmprunts()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
