package com.example.tpbibliotheque.model;

public class Livre {
    private String codeISBN;
    private String titre;
    private String auteur;
    private int anneePublication;

    public Livre(String codeISBN, String titre, String auteur, int anneePublication) {
        this.codeISBN = codeISBN;
        this.titre = titre;
        this.auteur = auteur;
        this.anneePublication = anneePublication;
    }

    public Livre() {
    }

    public String getCodeISBN() {
        return codeISBN;
    }

    public void setCodeISBN(String codeISBN) {
        this.codeISBN = codeISBN;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public int getAnneePublication() {
        return anneePublication;
    }

    public void setAnneePublication(int anneePublication) {
        this.anneePublication = anneePublication;
    }

    @Override
    public String toString() {
        return titre;
    }
}
