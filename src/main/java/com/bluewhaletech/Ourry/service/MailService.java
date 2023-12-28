package com.bluewhaletech.Ourry.service;

import com.bluewhaletech.Ourry.dto.EmailDTO;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;

public interface MailService {
    String sendMail(EmailDTO dto) throws MessagingException, UnsupportedEncodingException;
}
