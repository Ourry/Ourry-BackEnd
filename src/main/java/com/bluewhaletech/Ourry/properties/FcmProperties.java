package com.bluewhaletech.Ourry.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@Getter
@ConfigurationProperties(prefix = "fcm")
public class FcmProperties {
    private final String apiUrl;
    private final String secretKey;

    @ConstructorBinding
    public FcmProperties(String apiUrl, String secretKey) {
        this.apiUrl = apiUrl;
        this.secretKey = secretKey;
    }
}