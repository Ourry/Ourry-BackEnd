package com.bluewhaletech.Ourry.config;

import com.bluewhaletech.Ourry.properties.JwtProperties;
import com.bluewhaletech.Ourry.jwt.JwtProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {
    @Bean
    public JwtProvider tokenProvider(JwtProperties properties) {
        return new JwtProvider(properties.getSecret(), properties.getAtk(), properties.getRtk());
    }
}