package com.example.tpbibliotheque.Utils;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.example.tpbibliotheque.model.Emprunt;


import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PDFUtil {

    public static File genererPDFRecu(Emprunt emprunt) throws Exception {
        String fileName = "Recu_Emprunt_" + emprunt.getEleve().getIdEleve() + "_" + emprunt.getLivre().getCodeISBN() + ".pdf";
        File pdfFile = new File(System.getProperty("java.io.tmpdir"), fileName);

        PdfWriter writer = new PdfWriter(pdfFile);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        document.add(new Paragraph("Reçu d'emprunt - Bibliothèque"));
        document.add(new Paragraph("Élève : " + emprunt.getEleve().getNom() + " " + emprunt.getEleve().getPrenom()));
        document.add(new Paragraph("Livre : " + emprunt.getLivre().getTitre()));
        document.add(new Paragraph("Date d'emprunt : " + emprunt.getDateEmprunt().format(DateTimeFormatter.ISO_DATE)));

        LocalDate dateRetour = emprunt.getDateEmprunt().plusDays(14);
        document.add(new Paragraph("Date de retour prévue : " + dateRetour.format(DateTimeFormatter.ISO_DATE)));

        document.add(new Paragraph("\nMerci pour votre visite !"));

        document.close();
        return pdfFile;
    }
}
