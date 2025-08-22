package com.example.substracker.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.lang.Nullable;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PdfMailService {
    private final JavaMailSender mailSender;


    //Mshari
    public void sendHtmlEmail(String to, String subject, String htmlBody, @Nullable String... cc) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
            helper.setTo(to);
            if (cc != null && cc.length > 0) helper.setCc(cc);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException | MailException ex) {
            System.err.println("Failed to send HTML email: " + ex.getMessage());
        }
    }

    public void sendHtmlEmailWithAttachment(
            String to,
            String subject,
            String htmlBody,
            String attachmentFilename,
            byte[] pdfBytes,
            @Nullable String... cc
    ) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            // multipart=true to allow attachments
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(to);
            if (cc != null && cc.length > 0) helper.setCc(cc);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            helper.addAttachment(attachmentFilename, new ByteArrayResource(pdfBytes), "application/pdf");
            mailSender.send(mimeMessage);
        } catch (MessagingException | MailException ex) {
            System.err.println("Failed to send HTML email with attachment: " + ex.getMessage());
        }
    }
}
