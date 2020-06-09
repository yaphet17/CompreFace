package com.exadel.frs.helpers;

import com.exadel.frs.exception.UnreachableEmailException;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSender {

    @Value("${spring.mail.sender}")
    private String frsEmail;

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendMail(String to, String subject, String message)  {
        val msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setFrom(frsEmail);
        msg.setSubject(subject);
        msg.setText(message);

        try {
            javaMailSender.send(msg);
        } catch (MailException e) {
            throw new UnreachableEmailException(to);
        }
    }

}