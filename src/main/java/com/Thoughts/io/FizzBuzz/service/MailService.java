package com.Thoughts.io.FizzBuzz.service;

import com.Thoughts.io.FizzBuzz.exception.FizzBuzzException;
import com.Thoughts.io.FizzBuzz.model.NotificationEmail;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
@AllArgsConstructor
@Slf4j
class MailService {

    private final JavaMailSender mailSender;

    @Async
    void sendMail(NotificationEmail notificationEmail) {
        String host="smtp.gmail.com";
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port","465");
        properties.put("mail.smtp.ssl.enable","true");
        properties.put("mail.smtp.auth","true");

        Session session=Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("sonalsinha21222324@gmail.com", "Prity@24");
            }
        });

        session.setDebug(true);
        MimeMessage m = new MimeMessage(session);

        try {
            m.setFrom("FizzBuzz");
            m.addRecipient(Message.RecipientType.TO,new InternetAddress(notificationEmail.getRecipient()));
            m.setSubject(notificationEmail.getSubject());
            m.setText(notificationEmail.getBody());
            Transport.send(m);
            log.info("Activation email sent!!");
        } catch (MailException | MessagingException e) {
            throw new FizzBuzzException("Exception occurred when sending mail to " + notificationEmail.getRecipient());
        }
    }

}
