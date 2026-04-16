package com.example.rest.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.example.rest.service.INotificationService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
@RequiredArgsConstructor
public class SendEmailServiceImpl implements INotificationService {

    private final JavaMailSender mailSender;
    private static final String WELCOME_SUBJECT = "Welcome!";
    public static final String RESET_PASSWORD_SUBJECT = "Reset Password!";
    private static final String LOGIN_URL = "http://localhost:8083/loginPage";

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendEmail(String to, String subject, String content) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(buildContent(to, subject, content), true);
            mailSender.send(mimeMessage);
            log.info("Email sent to {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}", to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private String buildContent(String to, String subject, String content) {
        if (RESET_PASSWORD_SUBJECT.equals(subject)) {
            try (InputStream inputStream = new ClassPathResource("mail/reset-password.html").getInputStream()) {
                String template = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                return template
                        .replace("{{email}}", to)
                        .replace("{{resetUrl}}", content);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load reset password email template", e);
            }
        }
        if (!WELCOME_SUBJECT.equals(subject)) {
            return content;
        }

        try (InputStream inputStream = new ClassPathResource("mail/welcome-notification.html").getInputStream()) {
            String template = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return template
                    .replace("{{username}}", content)
                    .replace("{{email}}", to)
                    .replace("{{loginUrl}}", LOGIN_URL);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load welcome email template", e);
        }
    }

}
