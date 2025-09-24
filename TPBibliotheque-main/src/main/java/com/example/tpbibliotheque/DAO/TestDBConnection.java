package com.example.tpbibliotheque.DAO;



import java.sql.Connection;

public class TestDBConnection {
    public static void main(String[] args) {
        try {
            Connection conn = DBconnection.getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("Connexion réussie à la base de données !");                conn.close();            } else {                System.out.println("Connexion échouée.");            }        } catch (Exception e) {            System.out.println("Erreur lors de la connexion : " + e.getMessage());            e.printStackTrace();
        }
    }
}