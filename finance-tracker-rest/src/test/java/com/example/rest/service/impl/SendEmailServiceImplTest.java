package com.example.rest.service.impl;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Properties;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SendEmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private SendEmailServiceImpl service;

    @Test
    void sendEmailSendsPlainContentForNonTemplateSubject() {
        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        ReflectionTestUtils.setField(service, "fromEmail", "noreply@example.com");

        service.sendEmail("to@example.com", "Custom", "body");

        verify(mailSender).send(mimeMessage);
    }
}
