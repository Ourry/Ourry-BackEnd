package com.bluewhaletech.Ourry.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@Getter
@ConfigurationProperties(prefix = "fcm")
public class FcmProperties {
    private final String secretKey;
    private final String apiUrl;

    @ConstructorBinding
    public FcmProperties(String secretKey, String apiUrl) {
        this.secretKey = secretKey;
        this.apiUrl = apiUrl;
    }
}