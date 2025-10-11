package com.example.tpbibliotheque.DAO;

import com.example.tpbibliotheque.model.Eleve;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EleveDAO {
    Connection conn;

    public EleveDAO() {
        try {
            this.conn = DBconnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public EleveDAO(Connection conn) {
        this.conn = conn;
    }

    public void create(Eleve e) throws SQLException {
        String sql = "INSERT INTO eleve (nom, prenom, classe, email) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, e.getNom());
            stmt.setString(2, e.getPrenom());
            stmt.setString(3, e.getClasse());
            stmt.setString(4, e.getEmail());
            stmt.executeUpdate();
        }
    }

    public List<Eleve> readAll() throws SQLException {
        List<Eleve> list = new ArrayList<>();
        String sql = "SELECT * FROM eleve";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Eleve e = new Eleve();
                e.setIdEleve(rs.getInt("id_eleve"));
                e.setNom(rs.getString("nom"));
                e.setPrenom(rs.getString("prenom"));
                e.setClasse(rs.getString("classe"));
                e.setEmail(rs.getString("email"));
                list.add(e);
            }
        }
        return list;
    }

    public void update(Eleve e) throws SQLException {
        String sql = "UPDATE eleve SET nom = ?, prenom = ?, classe = ?, email = ? WHERE id_eleve = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, e.getNom());
            stmt.setString(2, e.getPrenom());
            stmt.setString(3, e.getClasse());
            stmt.setString(4, e.getEmail());
            stmt.setInt(5, e.getIdEleve());
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM eleve WHERE id_eleve = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public Optional<Eleve> findById(int id) {
        String sql = "SELECT * FROM eleve WHERE id_eleve = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Eleve e = new Eleve();
                e.setIdEleve(rs.getInt("id_eleve"));
                e.setNom(rs.getString("nom"));
                e.setPrenom(rs.getString("prenom"));
                e.setClasse(rs.getString("classe"));
                e.setEmail(rs.getString("email"));
                return Optional.of(e);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return Optional.empty();
    }

    // ✅ Méthode ajoutée pour compter les élèves (utilisée dans le Dashboard)
    public int countEleves() throws SQLException {
        String sql = "SELECT COUNT(*) FROM eleve";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
}
