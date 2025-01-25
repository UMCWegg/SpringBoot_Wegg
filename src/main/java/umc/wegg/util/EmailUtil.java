package umc.wegg.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailUtil {

    private final JavaMailSender javaMailSender;

    public EmailUtil(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public MimeMessage createMail(String senderEmail, String recipientEmail, String subject, String body) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        message.setFrom(senderEmail);
        message.setRecipients(MimeMessage.RecipientType.TO, recipientEmail);
        message.setSubject(subject);
        message.setText(body, "UTF-8", "html");

        return message;
    }

    public void sendMail(MimeMessage message) {
        javaMailSender.send(message);
    }
}
