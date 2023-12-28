package com.bluewhaletech.Ourry.service;

import com.bluewhaletech.Ourry.dto.EmailDTO;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;

@Slf4j
@Service
public class MailServiceImpl implements MailService {
    private final JavaMailSender mailSender;

    @Autowired
    public MailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Transactional
    @Override
    public String sendMail(EmailDTO dto) throws MessagingException, UnsupportedEncodingException {
        /* 이메일 발송 */
        MimeMessage message = createMessage(dto.getEmail(), dto.getTitle(), dto.getText());
        mailSender.send(message);
        return "SUCCESS";
    }

    private MimeMessage createMessage(String recipient, String title, String text) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        message.setFrom(new InternetAddress("iamddanddan@naver.com", "Ourry"));
        message.setRecipients(Message.RecipientType.TO, recipient);
        message.setSubject(title);
        message.setText(text, "UTF-8", "html");
        return message;
    }
}