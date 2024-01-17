package com.bluewhaletech.Ourry.service;

import com.bluewhaletech.Ourry.domain.Member;
import com.bluewhaletech.Ourry.dto.JwtDTO;
import com.bluewhaletech.Ourry.dto.TokenDTO;
import com.bluewhaletech.Ourry.jwt.JwtProvider;
import com.bluewhaletech.Ourry.util.RedisTokenManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuthServiceImpl implements AuthService {
    private final JwtProvider tokenProvider;
    private final RedisTokenManagement redisTokenManagement;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private static final String TOKEN_TYPE = "bearer";

    @Autowired
    public AuthServiceImpl(JwtProvider tokenProvider, RedisTokenManagement redisTokenManagement, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.tokenProvider = tokenProvider;
        this.redisTokenManagement = redisTokenManagement;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @Override
    public JwtDTO issueToken(Member member) {
        /* 오늘 날짜를 시간으로 변경해서 가져옴 */
        Long now = new Date().getTime();
        /* 이메일 & 비밀번호를 바탕으로 인증(Authentication) 정보 생성 */
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(
                new UsernamePasswordAuthenticationToken(member.getEmail(), member.getPassword())
        );

        /* Access Token 발급 */
        TokenDTO accessToken = tokenProvider.createAccessToken(authentication, now);
        /* Refresh Token 발급 */
        TokenDTO refreshToken = tokenProvider.createRefreshToken(now);
        /* Redis 내부에 RefreshToken 저장 */
        redisTokenManagement.storeRefreshToken(member.getEmail(), refreshToken.getToken(), refreshToken.getExpiration());

        return JwtDTO.builder()
                .type(TOKEN_TYPE)
                .accessToken(accessToken.getToken())
                .refreshToken(refreshToken.getToken())
                .accessTokenExpiration(new Date(now + accessToken.getExpiration()).getTime())
                .build();
    }
}