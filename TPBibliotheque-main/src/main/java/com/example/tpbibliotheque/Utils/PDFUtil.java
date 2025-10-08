package com.example.tpbibliotheque.Utils;

import com.example.tpbibliotheque.model.Emprunt;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.html2pdf.HtmlConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class PDFUtil {

    public static File genererPDFRecu(Emprunt emprunt) throws IOException, WriterException {
        File pdfFile = File.createTempFile("recu_emprunt_" + emprunt.getId(), ".pdf");

        String html;
        try (InputStream is = PDFUtil.class.getResourceAsStream(
                "/com/example/tpbibliotheque/templates/recu_template.html")) {
            if (is == null) {
                throw new FileNotFoundException("Template HTML introuvable !");
            }
            html = new String(is.readAllBytes());
        }

        String base64Logo = "";
        try (InputStream logoStream = PDFUtil.class.getResourceAsStream(
                "/com/example/tpbibliotheque/images/logo.png")) {
            if (logoStream != null) {
                base64Logo = Base64.getEncoder().encodeToString(logoStream.readAllBytes());
            } else {
                System.err.println("⚠️ Logo non trouvé dans resources/images/");
            }
        }


        String base64Signature = "";
        try (InputStream signatureStream = PDFUtil.class.getResourceAsStream(
                "/com/example/tpbibliotheque/images/signature.png")) {
            if (signatureStream != null) {
                base64Signature = Base64.getEncoder().encodeToString(signatureStream.readAllBytes());
            } else {
                System.err.println("⚠️ Signature non trouvée dans resources/images/");
            }
        }

        QRCodeWriter qrWriter = new QRCodeWriter();
        BitMatrix matrix = qrWriter.encode("ID_EMPRUNT:" + emprunt.getId(),
                BarcodeFormat.QR_CODE, 300, 300);
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(matrix);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "PNG", baos);
        String base64QR = Base64.getEncoder().encodeToString(baos.toByteArray());

        LocalDate dateRetour = emprunt.getDateRetour();
        if (dateRetour == null && emprunt.getDateEmprunt() != null) {
            dateRetour = emprunt.getDateEmprunt().plusWeeks(2);
        }

        String dateRetourStr = dateRetour != null
                ? dateRetour.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                : "Non définie";

        html = html.replace("{{logo}}", base64Logo)
                .replace("{{idemprunt}}", String.valueOf(emprunt.getId()))
                .replace("{{nom}}", emprunt.getEleve().getNom())
                .replace("{{prenom}}", emprunt.getEleve().getPrenom())
                .replace("{{livre}}", emprunt.getLivre().getTitre())
                .replace("{{dateRetour}}", dateRetourStr)
                .replace("{{qr}}", base64QR)
                .replace("{{signature}}", base64Signature)
                .replace("{{message}}", "L’emprunt de ce livre implique l’engagement de le restituer en bon état et avant la date indiquée. Tout retard ou dégradation pourra entraîner une suspension de prêt.");


        HtmlConverter.convertToPdf(html, new FileOutputStream(pdfFile));

        System.out.println("✅ PDF généré : " + pdfFile.getAbsolutePath());
        return pdfFile;
    }
}
