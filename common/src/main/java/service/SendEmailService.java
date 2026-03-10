package service;

public interface SendEmailService {

    void sendEmail(String to, String subject, String content);
}
