package com.example.tpbibliotheque.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconnection {
    public Connection lien;

    public static Connection getConnection() throws SQLException {
        String url = "jdbc:postgresql://postgresql-dimitrimeyepa.alwaysdata.net:5432/dimitrimeyepa_biblio";
        String user = "dimitrimeyepa";
        String password = "Dimitri2005@";
        return DriverManager.getConnection(url, user, password);
    }

}
