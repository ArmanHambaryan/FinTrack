package service;

import jakarta.mail.MessagingException;

public interface SendEmailService {

    void sendEmail(String to, String subject, String content);



}


