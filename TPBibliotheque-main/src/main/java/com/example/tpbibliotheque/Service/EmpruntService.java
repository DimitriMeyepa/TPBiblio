package com.example.tpbibliotheque.Service;

import com.example.tpbibliotheque.DAO.EleveDAO;
import com.example.tpbibliotheque.DAO.LivreDAO;
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

    public EmpruntService(Connection conn, EleveDAO eleveDAO, EmailService emailService, LivreDAO livreDAO) {
        this.conn = conn;
        this.eleveDAO = eleveDAO;
        this.emailService = emailService;
        this.livreDAO = livreDAO;
    }

    public void validerEmprunt(int eleveId, String livreCodeISBN) throws Exception {

        try (var ps = conn.prepareStatement(
                "INSERT INTO emprunt(id_eleve, code_isbn, date_emprunt) VALUES (?, ?, now())"
        )) {
            ps.setInt(1, eleveId);
            ps.setString(2, livreCodeISBN);
            ps.executeUpdate();
        }

        var eleveOpt = eleveDAO.findById(eleveId);
        var livreOpt = livreDAO.findByCodeISBN(livreCodeISBN);

        if (eleveOpt.isPresent() && livreOpt.isPresent()) {
            String email = eleveOpt.get().getEmail();
            String eleveNom = eleveOpt.get().getNom();
            String elevePrenom = eleveOpt.get().getPrenom();
            String titreLivre = livreOpt.get().getTitre();

            if (email != null && !email.isEmpty()) {
                String sujet = "Confirmation d’emprunt de livre";
                String texte = "Bonjour " + elevePrenom + " " + eleveNom + ",\n\n" +
                        "Votre emprunt du livre \"" + titreLivre + "\" a été enregistré avec succès.\n" +
                        "Merci de respecter la date de retour.\n\nCordialement,\nLe CDI";

                String html = "<html><body><p>Bonjour " + elevePrenom + " " + eleveNom + ",</p>" +
                        "<p>Votre <b>emprunt</b> du livre <b>\"" + titreLivre + "\"</b> a été enregistré avec succès.</p>" +
                        "<p>Merci de respecter la date de retour.</p>" +
                        "<p>Cordialement,<br><b>Le CDI</b></p></body></html>";


                Emprunt emprunt = new Emprunt();
                emprunt.setEleve(eleveOpt.get());
                emprunt.setLivre(livreOpt.get());
                emprunt.setDateEmprunt(LocalDate.now());

                File pdf = PDFUtil.genererPDFRecu(emprunt);

                new Thread(() -> {
                    try {
                        emailService.sendLoanEmailWithAttachment(email, sujet, texte, html, pdf);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }
    }
}
