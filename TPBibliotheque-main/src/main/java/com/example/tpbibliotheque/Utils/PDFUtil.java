package com.example.tpbibliotheque.Utils;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Image;
import com.itextpdf.io.image.ImageDataFactory;

import com.example.tpbibliotheque.model.Emprunt;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import javax.imageio.ImageIO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PDFUtil {

    public static File genererPDFRecu(Emprunt emprunt) throws Exception {
        // Nom et emplacement du PDF
        String fileName = "Recu_Emprunt_" + emprunt.getEleve().getIdEleve() + "_" + emprunt.getLivre().getCodeISBN() + ".pdf";
        File pdfFile = new File(System.getProperty("java.io.tmpdir"), fileName);

        PdfWriter writer = new PdfWriter(pdfFile);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Infos principales
        document.add(new Paragraph("Reçu d'emprunt - Bibliothèque"));
        document.add(new Paragraph("Élève : " + emprunt.getEleve().getNom() + " " + emprunt.getEleve().getPrenom()));
        document.add(new Paragraph("Livre : " + emprunt.getLivre().getTitre()));
        document.add(new Paragraph("Date d'emprunt : " + emprunt.getDateEmprunt().format(DateTimeFormatter.ISO_DATE)));

        LocalDate dateRetour = emprunt.getDateEmprunt().plusDays(14);
        document.add(new Paragraph("Date de retour prévue : " + dateRetour.format(DateTimeFormatter.ISO_DATE)));

        document.add(new Paragraph("\nMerci pour votre visite !"));

        // Génération QR Code
        String conditions = "Cher " + emprunt.getEleve().getNom() + " " + emprunt.getEleve().getPrenom()
                + ",\nVoici vos conditions d'emprunt :\n"
                + "- Durée maximale : 14 jours\n"
                + "- Pénalité de retard : 500 Rs par jour\n"
                + "- Le livre doit être rendu en bon état.\n";

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(conditions, BarcodeFormat.QR_CODE, 200, 200);
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "PNG", baos);
        byte[] qrBytes = baos.toByteArray();

        Image qrCode = new Image(ImageDataFactory.create(qrBytes));
        document.add(new Paragraph("\nConditions d'emprunt (voir QR code) :"));
        document.add(qrCode);

        // Ajout de la signature électronique depuis resources
        InputStream is = PDFUtil.class.getResourceAsStream("/com/example/tpbibliotheque/images/signature.png");
        if (is == null) {
            throw new RuntimeException("Impossible de trouver la signature dans resources/images");
        }
        Image signature = new Image(ImageDataFactory.create(is.readAllBytes()));
        signature.scaleToFit(500, 200); // Taille déjà ajustée
        document.add(new Paragraph("\nSignature :"));
        document.add(signature);

        document.close();
        return pdfFile;
    }
}
