package com.example.tpbibliotheque.DAO;

import com.example.tpbibliotheque.model.Livre;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class LivreDAO {
    Connection conn;

    public LivreDAO(Connection conn) {
        this.conn = conn;
    }

    public void create(Livre l) throws SQLException {
        String sql = "INSERT INTO livre (code_isbn, titre, auteur, annee_publication) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, l.getCodeISBN());
            stmt.setString(2, l.getTitre());
            stmt.setString(3, l.getAuteur());
            stmt.setInt(4, l.getAnneePublication());
            stmt.executeUpdate();
        }
    }

    public List<Livre> readAll() throws SQLException {
        List<Livre> list = new ArrayList<>();
        String sql = "SELECT * FROM livre";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Livre l = new Livre();
                l.setCodeISBN(rs.getString("code_isbn"));
                l.setTitre(rs.getString("titre"));
                l.setAuteur(rs.getString("auteur"));
                l.setAnneePublication(rs.getInt("annee_publication"));
                list.add(l);
            }
        }
        return list;
    }

    public void update(Livre l) throws SQLException {
        String sql = "UPDATE livre SET titre = ?, auteur = ?, annee_publication = ? WHERE code_isbn = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, l.getTitre());
            stmt.setString(2, l.getAuteur());
            stmt.setInt(3, l.getAnneePublication());
            stmt.setString(4, l.getCodeISBN());
            stmt.executeUpdate();
        }
    }

    public void delete(String isbn) throws SQLException {

        String checkSql = "SELECT COUNT(*) FROM emprunt WHERE code_isbn = ? AND date_retour IS NULL";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, isbn);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    if (count > 0) {
                        throw new SQLException("Impossible de supprimer un livre non retourné.");
                    }
                }
            }
        }


        String sql = "DELETE FROM livre WHERE code_isbn = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            stmt.executeUpdate();
        }
    }
    public Optional<Livre> findByCodeISBN(String codeISBN) {
        String sql = "SELECT * FROM livre WHERE code_isbn = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codeISBN);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Livre l = new Livre();
                    l.setCodeISBN(rs.getString("code_isbn"));
                    l.setTitre(rs.getString("titre"));
                    l.setAuteur(rs.getString("auteur"));
                    l.setAnneePublication(rs.getInt("annee_publication"));
                    return Optional.of(l);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return Optional.empty();
    }
    public boolean isEmprunte(String codeISBN) throws SQLException {
        String sql = "SELECT COUNT(*) FROM emprunt WHERE code_isbn = ? AND date_retour IS NULL";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codeISBN);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        }
    }


}
