package com.bluewhaletech.Ourry.jwt;

import com.bluewhaletech.Ourry.dto.AuthenticationDTO;
import com.bluewhaletech.Ourry.dto.JwtDTO;
import com.bluewhaletech.Ourry.exception.AuthorizationNotFoundException;
import com.bluewhaletech.Ourry.security.CustomUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {
    private final SecretKey secretKey;
    private final Long accessTokenExpiration;
    private final Long refreshTokenExpiration;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TOKEN_TYPE = "Bearer";

    @Autowired
    public JwtProvider(@Value("${jwt.secret}") String secret, @Value("${jwt.atk}") Long accessTokenExpiration, @Value("${jwt.rtk}") Long refreshTokenExpiration) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public JwtDTO createToken(AuthenticationDTO dto) {
        /* 오늘 날짜를 시간으로 변경해서 가져옴 */
        long now = new Date(System.currentTimeMillis()).getTime();

        /* 인증(Authentication) 정보로부터 권한 목록 불러오기 */
        String authorities = dto.getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        /* Access Token 생성 */
        String accessToken = Jwts.builder()
                .subject(dto.getTokenName())
                .claim(AUTHORIZATION_HEADER, authorities)
                .issuedAt(new Date(now))
                .expiration(new Date(now + accessTokenExpiration))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();

        /* Refresh Token 생성 */
        String refreshToken = Jwts.builder()
                .subject(String.valueOf(dto.getTokenId()))
                .issuedAt(new Date(now))
                .expiration(new Date(now + refreshTokenExpiration))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();

        return JwtDTO.builder()
                .tokenType(TOKEN_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiration(accessTokenExpiration)
                .refreshTokenExpiration(refreshTokenExpiration)
                .build();
    }

    private Jws<Claims> getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
    }

    public Long getTokenId(String refreshToken) {
        return Long.parseLong(getClaims(refreshToken).getPayload().getSubject());
    }

    /* Access Token 복호화를 통한 인증(Authentication) 정보 가져오기 */
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token).getPayload();
        if(claims.get(AUTHORIZATION_HEADER) == null) {
            throw new AuthorizationNotFoundException("권한 정보가 존재하지 않는 토큰입니다");
        }

        /* 권한(Authority) 정보 가져오기 */
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORIZATION_HEADER).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        /* CustomUser 객체를 만들어서 인증(Authentication) 정보 반환 */
        CustomUser principal = new CustomUser(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    /* Access Token 유효성 체크 */
    public boolean validateAccessToken(String token) {
        Claims claims = getClaims(token).getPayload();
        Date expiredDate = claims.getExpiration();
        return expiredDate.after(new Date());
    }

    /* Token 전송을 위한 Response Header 설정 */
    public void setResponseHeader(HttpServletResponse response, String header, String token) {
        response.setHeader(header, TOKEN_TYPE+" "+token);
    }
}