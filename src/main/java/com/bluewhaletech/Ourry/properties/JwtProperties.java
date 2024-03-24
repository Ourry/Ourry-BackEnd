package com.bluewhaletech.Ourry.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@Getter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private final String secret;
    private final Long atk;
    private final Long rtk;

    @ConstructorBinding
    public JwtProperties(String secret, Long atk, Long rtk) {
        this.secret = secret;
        this.atk = atk;
        this.rtk = rtk;
    }
}