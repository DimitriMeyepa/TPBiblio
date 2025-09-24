package com.example.tpbibliotheque.Service;

public class TestEmail {
    public static void main(String[] args) {
        try {
            EmailService emailService = new EmailService(
                    "dimitri.meyepa1@gmail.com",
                    "hqjwupurbepcuqkb",
                    false // ou true si tu veux SSL 465
            );

            emailService.sendLoanEmail(
                    "dimitrimeyepa5@gmail.com",
                    "Test Email CDI 📖",
                    "Bonjour, ceci est un test simple.",
                    "<p><b>Bonjour</b>, ceci est un test en HTML.</p>"
            );

            System.out.println("✅ Mail envoyé avec succès !");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
