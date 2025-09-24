package com.example.tpbibliotheque.DAO;

import com.example.tpbibliotheque.model.Eleve;
import com.example.tpbibliotheque.model.Emprunt;
import com.example.tpbibliotheque.model.Livre;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmpruntDAO {

    private final Connection conn;

    public EmpruntDAO(Connection conn) {
        this.conn = conn;
    }

    public List<Emprunt> readAll() throws SQLException {
        List<Emprunt> list = new ArrayList<>();
        String sql = "SELECT e.id, e.date_emprunt, e.date_retour, "
                + "el.id_eleve AS eleve_id, el.nom, el.prenom, "
                + "l.code_isbn, l.titre "
                + "FROM emprunt e "
                + "JOIN eleve el ON e.id_eleve = el.id_eleve "
                + "JOIN livre l ON e.code_isbn = l.code_isbn";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int idEmprunt = rs.getInt("id");
                LocalDate dateEmprunt = rs.getDate("date_emprunt").toLocalDate();

                Date dateRetourSql = rs.getDate("date_retour");
                LocalDate dateRetour = (dateRetourSql != null) ? dateRetourSql.toLocalDate() : null;

                Eleve eleve = new Eleve();
                eleve.setIdEleve(rs.getInt("eleve_id"));
                eleve.setNom(rs.getString("nom"));
                eleve.setPrenom(rs.getString("prenom"));

                Livre livre = new Livre();
                livre.setCodeISBN(rs.getString("code_isbn"));
                livre.setTitre(rs.getString("titre"));

                Emprunt emprunt = new Emprunt();
                emprunt.setId(idEmprunt);
                emprunt.setDateEmprunt(dateEmprunt);
                emprunt.setDateRetour(dateRetour);
                emprunt.setEleve(eleve);
                emprunt.setLivre(livre);

                list.add(emprunt);
            }
        }

        return list;
    }

    public void updateRetour(int idEmprunt, LocalDate dateRetour) throws SQLException {
        String sql = "UPDATE emprunt SET date_retour = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(dateRetour));
            ps.setInt(2, idEmprunt);
            ps.executeUpdate();
        }
    }

    public boolean canEmprunter(String codeISBN) throws SQLException {
        String sql = "SELECT COUNT(*) FROM emprunt WHERE code_isbn = ? AND date_retour IS NULL";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codeISBN);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    System.out.println("Emprunts actifs pour le livre " + codeISBN + ": " + count);
                    return count == 0;
                }
            }
        }
        return false;
    }

    public void create(Emprunt e) throws SQLException {
        if (!canEmprunter(e.getLivre().getCodeISBN())) {
            throw new SQLException("Livre déjà emprunté, impossible d'emprunter.");
        }

        String sql = "INSERT INTO emprunt (id_eleve, code_isbn, date_emprunt, date_retour) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, e.getEleve().getIdEleve());
            ps.setString(2, e.getLivre().getCodeISBN());
            ps.setDate(3, Date.valueOf(e.getDateEmprunt()));

            if (e.getDateRetour() != null) {
                ps.setDate(4, Date.valueOf(e.getDateRetour()));
            } else {
                ps.setNull(4, java.sql.Types.DATE);
            }

            ps.executeUpdate();
        }
    }
}
