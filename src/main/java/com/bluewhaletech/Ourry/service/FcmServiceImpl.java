package com.bluewhaletech.Ourry.service;

import com.bluewhaletech.Ourry.dto.FcmRequestDTO;
import com.bluewhaletech.Ourry.dto.FcmMessageDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class FcmServiceImpl implements FcmService {
    private final String apiUrl;
    private final String secretKey;
    private final ObjectMapper objectMapper;

    @Autowired
    public FcmServiceImpl(@Value("${fcm.apiUrl}") String apiUrl, @Value("${fcm.secretKey}") String secretKey, ObjectMapper objectMapper) {
        this.apiUrl = apiUrl;
        this.secretKey = secretKey;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        try {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(new ClassPathResource(secretKey).getInputStream()))
                    .build();
            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void sendMessage(String targetToken, String title, String body) throws IOException {
        FcmRequestDTO dto = FcmRequestDTO.builder()
                .targetToken(targetToken)
                .title(title)
                .body(body)
                .build();

        HttpURLConnection conn = null;
        try {
            URL url = new URL(apiUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "Bearer " + getAccessToken());
            conn.setRequestProperty("Content-Type", "application/json; UTF-8");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
        } catch (IOException e) {
            log.error("FCM Service Connection Exception : " + e.getMessage());
        }

        DataOutputStream dataOutputStream = null;
        try {
            Optional.ofNullable(conn).orElseThrow(MalformedURLException::new);
            dataOutputStream = new DataOutputStream(conn.getOutputStream());
            dataOutputStream.writeBytes(makeMessage(dto));
            dataOutputStream.flush();
        } catch (Exception e) {
            log.error("FCM Service Request Exception : " + e.getMessage());
        } finally {
            if(dataOutputStream != null) dataOutputStream.close();
        }
    }

    private String makeMessage(FcmRequestDTO request) throws JsonProcessingException {
        Notification notification = Notification.builder()
                .setTitle(request.getTitle())
                .setBody(request.getBody())
                .build();
        Message message = Message.builder()
                .setNotification(notification)
                .setToken(request.getTargetToken())
                .build();
        FcmMessageDTO dto = FcmMessageDTO.builder()
                .validateOnly(false)
                .message(message)
                .build();
        return objectMapper.writeValueAsString(dto);
    }

    private String getAccessToken() throws IOException {
        try {
            GoogleCredentials googleCredentials = GoogleCredentials
                    .fromStream(new ClassPathResource(secretKey).getInputStream())
                    .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
            googleCredentials.refreshIfExpired();
            return googleCredentials.getAccessToken().getTokenValue();
        } catch (IOException e) {
            log.error("Firebase Access-Token Request Exception : " + e.getMessage());
            throw new IOException();
        }
    }
}