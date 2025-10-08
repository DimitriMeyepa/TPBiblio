package com.example.tpbibliotheque.Service;

import com.example.tpbibliotheque.DAO.EleveDAO;
import com.example.tpbibliotheque.DAO.LivreDAO;
import com.example.tpbibliotheque.DAO.EmpruntDAO;
import com.example.tpbibliotheque.Utils.PDFUtil;
import com.example.tpbibliotheque.model.Emprunt;
import jakarta.mail.MessagingException;

import java.io.File;
import java.sql.Connection;
import java.time.LocalDate;

public class EmpruntService {

    private final Connection conn;
    private final EleveDAO eleveDAO;
    private final EmailService emailService;
    private final LivreDAO livreDAO;
    private final EmpruntDAO empruntDAO; // ✅ On ajoute le DAO ici

    public EmpruntService(Connection conn, EleveDAO eleveDAO, EmailService emailService, LivreDAO livreDAO) {
        this.conn = conn;
        this.eleveDAO = eleveDAO;
        this.emailService = emailService;
        this.livreDAO = livreDAO;
        this.empruntDAO = new EmpruntDAO(conn); // ✅ Initialisation
    }

    public void validerEmprunt(int eleveId, String livreCodeISBN) throws Exception {

        var eleveOpt = eleveDAO.findById(eleveId);
        var livreOpt = livreDAO.findByCodeISBN(livreCodeISBN);

        if (eleveOpt.isPresent() && livreOpt.isPresent()) {
            var eleve = eleveOpt.get();
            var livre = livreOpt.get();

            // ✅ Création de l'objet Emprunt
            Emprunt emprunt = new Emprunt();
            emprunt.setEleve(eleve);
            emprunt.setLivre(livre);
            emprunt.setDateEmprunt(LocalDate.now());

            // ✅ Appel du DAO qui insère et récupère l’ID auto-généré
            empruntDAO.create(emprunt);

            System.out.println("✅ Emprunt créé avec ID : " + emprunt.getId());

            // ✅ Génération du PDF avec le vrai ID
            File pdf = PDFUtil.genererPDFRecu(emprunt);

            // ✅ Envoi du mail
            if (eleve.getEmail() != null && !eleve.getEmail().isEmpty()) {
                String sujet = "Confirmation d’emprunt de livre";
                String texte = "Bonjour " + eleve.getPrenom() + " " + eleve.getNom() + ",\n\n" +
                        "Votre emprunt du livre \"" + livre.getTitre() + "\" a été enregistré avec succès.\n" +
                        "Merci de respecter la date de retour.\n\nCordialement,\nLe CDI";

                String html = "<html><body><p>Bonjour " + eleve.getPrenom() + " " + eleve.getNom() + ",</p>" +
                        "<p>Votre <b>emprunt</b> du livre <b>\"" + livre.getTitre() + "\"</b> a été enregistré avec succès.</p>" +
                        "<p>Merci de respecter la date de retour.</p>" +
                        "<p>Cordialement,<br><b>Le CDI</b></p></body></html>";

                new Thread(() -> {
                    try {
                        emailService.sendLoanEmailWithAttachment(eleve.getEmail(), sujet, texte, html, pdf);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        } else {
            throw new Exception("Élève ou livre introuvable.");
        }
    }
}
