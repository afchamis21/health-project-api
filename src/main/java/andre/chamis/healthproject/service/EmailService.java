package andre.chamis.healthproject.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for sending emails asynchronously.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${spring.mail.username}")
    private String fromEmail;
    private final JavaMailSender emailSender;

    /**
     * Asynchronously sends an email with the specified content.
     *
     * @param to      The email address of the recipient.
     * @param message The content of the email.
     * @param subject The subject of the email.
     */
    @Async
    public void sendSimpleMail(String to, String message, String subject) {
        log.info("Sending email to [{}]. Subject [{}]. Message [{}].", to, subject, message);
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setSubject(subject);
        mailMessage.setFrom(fromEmail);
        mailMessage.setTo(to);
        mailMessage.setText(message);
        emailSender.send(mailMessage);
    }
}
