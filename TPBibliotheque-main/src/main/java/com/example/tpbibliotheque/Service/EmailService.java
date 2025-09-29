package com.example.tpbibliotheque.Service;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class EmailService {

    private final String username;
    private final String appPassword;
    private final boolean useSSL;

    public EmailService(String username, String appPassword, boolean useSSL) {
        this.username = username;
        this.appPassword = appPassword;
        this.useSSL = useSSL;
    }

    private Session session() {
        String host = "smtp.gmail.com";
        String port = useSSL ? "465" : "587";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        if (useSSL) {
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.ssl.checkserveridentity", "true");
            props.put("mail.smtp.ssl.trust", host);
        } else {
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
            props.put("mail.smtp.ssl.trust", host);
        }

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, appPassword);
            }
        });
    }

    public void sendLoanEmail(String to, String sujet, String texte, String html) throws MessagingException {
        MimeMessage msg = new MimeMessage(session());
        msg.setFrom(new InternetAddress(username));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
        msg.setSubject(sujet, StandardCharsets.UTF_8.name());

        MimeMultipart alternative = new MimeMultipart("alternative");

        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(texte, StandardCharsets.UTF_8.name());
        alternative.addBodyPart(textPart);

        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(html, "text/html; charset=UTF-8");
        alternative.addBodyPart(htmlPart);

        msg.setContent(alternative);
        msg.saveChanges();

        Transport.send(msg);
    }

    public void sendLoanEmailWithAttachment(String to, String sujet, String texte, String html, File pdf)
            throws Exception {
        MimeMessage msg = new MimeMessage(session());
        msg.setFrom(new InternetAddress(username));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
        msg.setSubject(sujet, StandardCharsets.UTF_8.name());

        MimeMultipart multipart = new MimeMultipart();

        MimeBodyPart alternativePart = new MimeBodyPart();
        MimeMultipart alternative = new MimeMultipart("alternative");

        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(texte, StandardCharsets.UTF_8.name());
        alternative.addBodyPart(textPart);

        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(html, "text/html; charset=UTF-8");
        alternative.addBodyPart(htmlPart);

        alternativePart.setContent(alternative);
        multipart.addBodyPart(alternativePart);


        MimeBodyPart attachmentPart = new MimeBodyPart();
        attachmentPart.attachFile(pdf);
        multipart.addBodyPart(attachmentPart);

        msg.setContent(multipart);
        msg.saveChanges();

        Transport.send(msg);
    }
}
