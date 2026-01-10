package com.mrt.services;

import java.io.File;
import java.util.Properties;

import com.mrt.models.EmailMessage;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

public class EmailService {

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;

    private static Dotenv dotenv = Dotenv.load();
    private static final String HOST_EMAIL_ADDRESS = dotenv.get("HOST_EMAIL_ADDRESS");
    private static final String HOST_EMAIL_PASSWORD = dotenv.get("HOST_EMAIL_PASSWORD");

    public static void send(EmailMessage message) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);

            // System.out.println(fromEmail + " " + emailPassword);

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(HOST_EMAIL_ADDRESS, HOST_EMAIL_PASSWORD);
                }
            });

            MimeMessage mime = new MimeMessage(session);
            mime.setFrom(new InternetAddress(HOST_EMAIL_ADDRESS));
            mime.setRecipients(Message.RecipientType.TO, message.getRecipient());
            mime.setSubject(message.getSubject(), "UTF-8");

            Multipart multipart = new MimeMultipart();

            MimeBodyPart body = new MimeBodyPart();
            body.setContent(message.getBody(), "text/html; charset=UTF-8");
            multipart.addBodyPart(body);

            for(File file: message.getAttachments()) {
                MimeBodyPart attachment = new MimeBodyPart();
                attachment.attachFile(file);
                multipart.addBodyPart(attachment);
            }

            mime.setContent(multipart);
            Transport.send(mime);
        } catch(Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public static boolean isValidEmailAddress(String email) {
        boolean valid = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch(AddressException ignored) {
            valid = false;
        }
        return valid;
    }
}
