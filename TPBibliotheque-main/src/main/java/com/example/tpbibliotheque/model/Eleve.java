package com.example.tpbibliotheque.model;

public class Eleve {

    private int idEleve;
    private String nom;
    private String prenom;
    private String classe;
    private String email;
    @Override
    public String toString() {
        return nom + " " + prenom;
    }

    public Eleve(int idEleve, String nom, String prenom, String classe, String email) {
        this.idEleve = idEleve;
        this.nom = nom;
        this.prenom = prenom;
        this.classe = classe;
        this.email = email;
    }
    public Eleve(){}

    public int getIdEleve() {
        return idEleve;
    }

    public void setIdEleve(int idEleve) {
        this.idEleve = idEleve;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}



}
